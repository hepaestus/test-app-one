package com.hepaestus.testappone.repository.search;

import com.hepaestus.testappone.domain.Person;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Person} entity.
 */
public interface PersonSearchRepository extends ElasticsearchRepository<Person, Long> {}
