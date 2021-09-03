package com.hepaestus.testappone.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.hepaestus.testappone.domain.Driver;
import com.hepaestus.testappone.repository.DriverRepository;
import com.hepaestus.testappone.repository.search.DriverSearchRepository;
import com.hepaestus.testappone.service.dto.DriverDTO;
import com.hepaestus.testappone.service.mapper.DriverMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Driver}.
 */
@Service
@Transactional
public class DriverService {

    private final Logger log = LoggerFactory.getLogger(DriverService.class);

    private final DriverRepository driverRepository;

    private final DriverMapper driverMapper;

    private final DriverSearchRepository driverSearchRepository;

    public DriverService(DriverRepository driverRepository, DriverMapper driverMapper, DriverSearchRepository driverSearchRepository) {
        this.driverRepository = driverRepository;
        this.driverMapper = driverMapper;
        this.driverSearchRepository = driverSearchRepository;
    }

    /**
     * Save a driver.
     *
     * @param driverDTO the entity to save.
     * @return the persisted entity.
     */
    public DriverDTO save(DriverDTO driverDTO) {
        log.debug("Request to save Driver : {}", driverDTO);
        Driver driver = driverMapper.toEntity(driverDTO);
        driver = driverRepository.save(driver);
        DriverDTO result = driverMapper.toDto(driver);
        driverSearchRepository.save(driver);
        return result;
    }

    /**
     * Partially update a driver.
     *
     * @param driverDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<DriverDTO> partialUpdate(DriverDTO driverDTO) {
        log.debug("Request to partially update Driver : {}", driverDTO);

        return driverRepository
            .findById(driverDTO.getId())
            .map(
                existingDriver -> {
                    driverMapper.partialUpdate(existingDriver, driverDTO);

                    return existingDriver;
                }
            )
            .map(driverRepository::save)
            .map(
                savedDriver -> {
                    driverSearchRepository.save(savedDriver);

                    return savedDriver;
                }
            )
            .map(driverMapper::toDto);
    }

    /**
     * Get all the drivers.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<DriverDTO> findAll() {
        log.debug("Request to get all Drivers");
        return driverRepository.findAll().stream().map(driverMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one driver by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<DriverDTO> findOne(Long id) {
        log.debug("Request to get Driver : {}", id);
        return driverRepository.findById(id).map(driverMapper::toDto);
    }

    /**
     * Delete the driver by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Driver : {}", id);
        driverRepository.deleteById(id);
        driverSearchRepository.deleteById(id);
    }

    /**
     * Search for the driver corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<DriverDTO> search(String query) {
        log.debug("Request to search Drivers for query {}", query);
        return StreamSupport
            .stream(driverSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .map(driverMapper::toDto)
            .collect(Collectors.toList());
    }
}
