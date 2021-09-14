package com.hepaestus.testappone.service;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hepaestus.testappone.domain.*;
import com.hepaestus.testappone.repository.*;
import com.hepaestus.testappone.repository.search.*;
import com.tdunning.math.stats.Sort;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import javax.persistence.ManyToMany;
import org.elasticsearch.ResourceAlreadyExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ElasticsearchIndexService {

    private static final Lock reindexLock = new ReentrantLock();

    private final Logger log = LoggerFactory.getLogger(ElasticsearchIndexService.class);

    private final CarRepository carRepository;

    private final CarSearchRepository carSearchRepository;

    private final DriverRepository driverRepository;

    private final DriverSearchRepository driverSearchRepository;

    private final PersonRepository personRepository;

    private final PersonSearchRepository personSearchRepository;

    private final ShoeRepository shoeRepository;

    private final ShoeSearchRepository shoeSearchRepository;

    private final UserRepository userRepository;

    private final UserSearchRepository userSearchRepository;

    private final ElasticsearchOperations elasticsearchTemplate;

    public ElasticsearchIndexService(
        UserRepository userRepository,
        UserSearchRepository userSearchRepository,
        CarRepository carRepository,
        CarSearchRepository carSearchRepository,
        DriverRepository driverRepository,
        DriverSearchRepository driverSearchRepository,
        PersonRepository personRepository,
        PersonSearchRepository personSearchRepository,
        ShoeRepository shoeRepository,
        ShoeSearchRepository shoeSearchRepository,
        ElasticsearchOperations elasticsearchTemplate
    ) {
        this.userRepository = userRepository;
        this.userSearchRepository = userSearchRepository;
        this.carRepository = carRepository;
        this.carSearchRepository = carSearchRepository;
        this.driverRepository = driverRepository;
        this.driverSearchRepository = driverSearchRepository;
        this.personRepository = personRepository;
        this.personSearchRepository = personSearchRepository;
        this.shoeRepository = shoeRepository;
        this.shoeSearchRepository = shoeSearchRepository;
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Async
    @Timed
    public void reindexAll() {
        if (reindexLock.tryLock()) {
            try {
                reindexForClass(Car.class, carRepository, carSearchRepository);

                reindexForClass(Driver.class, driverRepository, driverSearchRepository);
                reindexForClass(Person.class, personRepository, personSearchRepository);
                reindexForClass(Shoe.class, shoeRepository, shoeSearchRepository);
                reindexForClass(User.class, userRepository, userSearchRepository);

                log.info("Elasticsearch: Successfully performed reindexing");
            } finally {
                reindexLock.unlock();
            }
        } else {
            log.info("Elasticsearch: concurrent reindexing attempt");
        }
    }

    @SuppressWarnings("unchecked")
    private <T, ID extends Serializable> void reindexForClass(
        Class<T> entityClass,
        JpaRepository<T, ID> jpaRepository,
        ElasticsearchRepository<T, ID> elasticsearchRepository
    ) {
        elasticsearchTemplate.deleteIndex(entityClass);
        try {
            elasticsearchTemplate.createIndex(entityClass);
        } catch (ResourceAlreadyExistsException e) {
            // Do nothing. Index was already concurrently recreated by some other service.
        }
        elasticsearchTemplate.putMapping(entityClass);
        if (jpaRepository.count() > 0) {
            // if a JHipster entity field is the owner side of a many-to-many relationship, it should be loaded manually
            List<Method> relationshipGetters = Arrays
                .stream(entityClass.getDeclaredFields())
                .filter(field -> field.getType().equals(Set.class))
                .filter(field -> field.getAnnotation(ManyToMany.class) != null)
                .filter(field -> field.getAnnotation(ManyToMany.class).mappedBy().isEmpty())
                .filter(field -> field.getAnnotation(JsonIgnore.class) == null)
                .filter(field -> field.getAnnotation(JsonIgnoreProperties.class) == null) // This line Added By Pete
                .map(
                    field -> {
                        log.debug("#### ELASTICSEARCH FIELD " + field.getName());
                        try {
                            return new PropertyDescriptor(field.getName(), entityClass).getReadMethod();
                        } catch (IntrospectionException e) {
                            log.error(
                                "#### Error retrieving getter for class {}, field {}. Field will NOT be indexed",
                                entityClass.getSimpleName(),
                                field.getName(),
                                e
                            );
                            return null;
                        }
                    }
                )
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

            int size = 100;
            for (int i = 0; i <= jpaRepository.count() / size; i++) {
                Pageable page = PageRequest.of(i, size);
                log.info("Indexing page {} of {}, size {}", i, jpaRepository.count() / size, size);
                Page<T> results = jpaRepository.findAll(page);
                results.map(
                    result -> {
                        // if there are any relationships to load, do it now
                        relationshipGetters.forEach(
                            method -> {
                                try {
                                    log.debug("#### FOREACH RELATIONSHIP GETTERS");
                                    // eagerly load the relationship set
                                    ((Set) method.invoke(result)).size();
                                } catch (Exception ex) {
                                    log.error("#### ERROR : {}", ex.getMessage());
                                }
                            }
                        );
                        return result;
                    }
                );
                elasticsearchRepository.saveAll(results.getContent());
            }
        }
        log.info("Elasticsearch: Indexed all rows for {}", entityClass.getSimpleName());
    }
}
