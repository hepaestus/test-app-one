package com.hepaestus.testappone.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.hepaestus.testappone.repository.ShoeRepository;
import com.hepaestus.testappone.service.ShoeService;
import com.hepaestus.testappone.service.dto.ShoeDTO;
import com.hepaestus.testappone.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.hepaestus.testappone.domain.Shoe}.
 */
@RestController
@RequestMapping("/api")
public class ShoeResource {

    private final Logger log = LoggerFactory.getLogger(ShoeResource.class);

    private static final String ENTITY_NAME = "shoe";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ShoeService shoeService;

    private final ShoeRepository shoeRepository;

    public ShoeResource(ShoeService shoeService, ShoeRepository shoeRepository) {
        this.shoeService = shoeService;
        this.shoeRepository = shoeRepository;
    }

    /**
     * {@code POST  /shoes} : Create a new shoe.
     *
     * @param shoeDTO the shoeDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new shoeDTO, or with status {@code 400 (Bad Request)} if the shoe has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/shoes")
    public ResponseEntity<ShoeDTO> createShoe(@Valid @RequestBody ShoeDTO shoeDTO) throws URISyntaxException {
        log.debug("REST request to save Shoe : {}", shoeDTO);
        if (shoeDTO.getId() != null) {
            throw new BadRequestAlertException("A new shoe cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ShoeDTO result = shoeService.save(shoeDTO);
        return ResponseEntity
            .created(new URI("/api/shoes/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /shoes/:id} : Updates an existing shoe.
     *
     * @param id the id of the shoeDTO to save.
     * @param shoeDTO the shoeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated shoeDTO,
     * or with status {@code 400 (Bad Request)} if the shoeDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the shoeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/shoes/{id}")
    public ResponseEntity<ShoeDTO> updateShoe(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ShoeDTO shoeDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Shoe : {}, {}", id, shoeDTO);
        if (shoeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, shoeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!shoeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ShoeDTO result = shoeService.save(shoeDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, shoeDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /shoes/:id} : Partial updates given fields of an existing shoe, field will ignore if it is null
     *
     * @param id the id of the shoeDTO to save.
     * @param shoeDTO the shoeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated shoeDTO,
     * or with status {@code 400 (Bad Request)} if the shoeDTO is not valid,
     * or with status {@code 404 (Not Found)} if the shoeDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the shoeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/shoes/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<ShoeDTO> partialUpdateShoe(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ShoeDTO shoeDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Shoe partially : {}, {}", id, shoeDTO);
        if (shoeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, shoeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!shoeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ShoeDTO> result = shoeService.partialUpdate(shoeDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, shoeDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /shoes} : get all the shoes.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of shoes in body.
     */
    @GetMapping("/shoes")
    public List<ShoeDTO> getAllShoes() {
        log.debug("REST request to get all Shoes");
        return shoeService.findAll();
    }

    /**
     * {@code GET  /shoes/:id} : get the "id" shoe.
     *
     * @param id the id of the shoeDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the shoeDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/shoes/{id}")
    public ResponseEntity<ShoeDTO> getShoe(@PathVariable Long id) {
        log.debug("REST request to get Shoe : {}", id);
        Optional<ShoeDTO> shoeDTO = shoeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(shoeDTO);
    }

    /**
     * {@code DELETE  /shoes/:id} : delete the "id" shoe.
     *
     * @param id the id of the shoeDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/shoes/{id}")
    public ResponseEntity<Void> deleteShoe(@PathVariable Long id) {
        log.debug("REST request to delete Shoe : {}", id);
        shoeService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/shoes?query=:query} : search for the shoe corresponding
     * to the query.
     *
     * @param query the query of the shoe search.
     * @return the result of the search.
     */
    @GetMapping("/_search/shoes")
    public List<ShoeDTO> searchShoes(@RequestParam String query) {
        log.debug("REST request to search Shoes for query {}", query);
        return shoeService.search(query);
    }
}
