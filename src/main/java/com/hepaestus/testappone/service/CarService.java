package com.hepaestus.testappone.service;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.hepaestus.testappone.domain.Car;
import com.hepaestus.testappone.repository.CarRepository;
import com.hepaestus.testappone.repository.search.CarSearchRepository;
import com.hepaestus.testappone.service.dto.CarDTO;
import com.hepaestus.testappone.service.mapper.CarMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Car}.
 */
@Service
@Transactional
public class CarService {

    private final Logger log = LoggerFactory.getLogger(CarService.class);

    private final CarRepository carRepository;

    private final CarMapper carMapper;

    private final CarSearchRepository carSearchRepository;

    public CarService(CarRepository carRepository, CarMapper carMapper, CarSearchRepository carSearchRepository) {
        this.carRepository = carRepository;
        this.carMapper = carMapper;
        this.carSearchRepository = carSearchRepository;
    }

    /**
     * Save a car.
     *
     * @param carDTO the entity to save.
     * @return the persisted entity.
     */
    public CarDTO save(CarDTO carDTO) {
        Car car = new Car();
        try {
            log.debug("Request to save Car : {}", carDTO);
            car = carMapper.toEntity(carDTO);
            car = carRepository.save(car);
        } catch (Exception e) {
            log.error("#### ERROR SAVE CAR DATABASE : " + e.getMessage());
        }

        try {
            log.debug("Request to save to Elasticsearch Car  : {}", carDTO);
            if (car.getId() != null) {
                CarDTO result = carMapper.toDto(car);
                carSearchRepository.save(car);
                return result;
            } else {
                log.error("#### ERROR SAVE NULL CAR");
            }
        } catch (Exception e) {
            log.error("#### ERROR SAVE CAR ELASTICSEARCH : " + e.getMessage());
            return null;
        }
        return null;
    }

    /**
     * Partially update a car.
     *
     * @param carDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<CarDTO> partialUpdate(CarDTO carDTO) {
        log.debug("Request to partially update Car : {}", carDTO);

        return carRepository
            .findById(carDTO.getId())
            .map(
                existingCar -> {
                    carMapper.partialUpdate(existingCar, carDTO);

                    return existingCar;
                }
            )
            .map(carRepository::save)
            .map(
                savedCar -> {
                    carSearchRepository.save(savedCar);

                    return savedCar;
                }
            )
            .map(carMapper::toDto);
    }

    /**
     * Get all the cars.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<CarDTO> findAll() {
        log.debug("Request to get all Cars");
        return carRepository
            .findAllWithEagerRelationships()
            .stream()
            .map(carMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get all the cars with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<CarDTO> findAllWithEagerRelationships(Pageable pageable) {
        return carRepository.findAllWithEagerRelationships(pageable).map(carMapper::toDto);
    }

    /**
     * Get one car by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<CarDTO> findOne(Long id) {
        log.debug("Request to get Car : {}", id);
        return carRepository.findOneWithEagerRelationships(id).map(carMapper::toDto);
    }

    /**
     * Delete the car by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Car : {}", id);
        carRepository.deleteById(id);
        carSearchRepository.deleteById(id);
    }

    /**
     * Search for the car corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<CarDTO> search(String query) {
        log.debug("Request to search Cars for query {}", query);
        return StreamSupport
            .stream(carSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .map(carMapper::toDto)
            .collect(Collectors.toList());
    }
}
