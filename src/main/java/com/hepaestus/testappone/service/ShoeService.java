package com.hepaestus.testappone.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.hepaestus.testappone.domain.Shoe;
import com.hepaestus.testappone.repository.ShoeRepository;
import com.hepaestus.testappone.repository.search.ShoeSearchRepository;
import com.hepaestus.testappone.service.dto.ShoeDTO;
import com.hepaestus.testappone.service.mapper.ShoeMapper;
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
 * Service Implementation for managing {@link Shoe}.
 */
@Service
@Transactional
public class ShoeService {

    private final Logger log = LoggerFactory.getLogger(ShoeService.class);

    private final ShoeRepository shoeRepository;

    private final ShoeMapper shoeMapper;

    private final ShoeSearchRepository shoeSearchRepository;

    public ShoeService(ShoeRepository shoeRepository, ShoeMapper shoeMapper, ShoeSearchRepository shoeSearchRepository) {
        this.shoeRepository = shoeRepository;
        this.shoeMapper = shoeMapper;
        this.shoeSearchRepository = shoeSearchRepository;
    }

    /**
     * Save a shoe.
     *
     * @param shoeDTO the entity to save.
     * @return the persisted entity.
     */
    public ShoeDTO save(ShoeDTO shoeDTO) {
        log.debug("Request to save Shoe : {}", shoeDTO);
        Shoe shoe = shoeMapper.toEntity(shoeDTO);
        shoe = shoeRepository.save(shoe);
        ShoeDTO result = shoeMapper.toDto(shoe);
        shoeSearchRepository.save(shoe);
        return result;
    }

    /**
     * Partially update a shoe.
     *
     * @param shoeDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ShoeDTO> partialUpdate(ShoeDTO shoeDTO) {
        log.debug("Request to partially update Shoe : {}", shoeDTO);

        return shoeRepository
            .findById(shoeDTO.getId())
            .map(
                existingShoe -> {
                    shoeMapper.partialUpdate(existingShoe, shoeDTO);

                    return existingShoe;
                }
            )
            .map(shoeRepository::save)
            .map(
                savedShoe -> {
                    shoeSearchRepository.save(savedShoe);

                    return savedShoe;
                }
            )
            .map(shoeMapper::toDto);
    }

    /**
     * Get all the shoes.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<ShoeDTO> findAll() {
        log.debug("Request to get all Shoes");
        return shoeRepository.findAll().stream().map(shoeMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one shoe by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ShoeDTO> findOne(Long id) {
        log.debug("Request to get Shoe : {}", id);
        return shoeRepository.findById(id).map(shoeMapper::toDto);
    }

    /**
     * Delete the shoe by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Shoe : {}", id);
        shoeRepository.deleteById(id);
        shoeSearchRepository.deleteById(id);
    }

    /**
     * Search for the shoe corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<ShoeDTO> search(String query) {
        log.debug("Request to search Shoes for query {}", query);
        return StreamSupport
            .stream(shoeSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .map(shoeMapper::toDto)
            .collect(Collectors.toList());
    }
}
