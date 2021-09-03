package com.hepaestus.testappone.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.hepaestus.testappone.IntegrationTest;
import com.hepaestus.testappone.domain.Shoe;
import com.hepaestus.testappone.repository.ShoeRepository;
import com.hepaestus.testappone.repository.search.ShoeSearchRepository;
import com.hepaestus.testappone.service.dto.ShoeDTO;
import com.hepaestus.testappone.service.mapper.ShoeMapper;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ShoeResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ShoeResourceIT {

    private static final Integer DEFAULT_SHOE_SIZE = 4;
    private static final Integer UPDATED_SHOE_SIZE = 5;

    private static final String ENTITY_API_URL = "/api/shoes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/shoes";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ShoeRepository shoeRepository;

    @Autowired
    private ShoeMapper shoeMapper;

    /**
     * This repository is mocked in the com.hepaestus.testappone.repository.search test package.
     *
     * @see com.hepaestus.testappone.repository.search.ShoeSearchRepositoryMockConfiguration
     */
    @Autowired
    private ShoeSearchRepository mockShoeSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restShoeMockMvc;

    private Shoe shoe;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Shoe createEntity(EntityManager em) {
        Shoe shoe = new Shoe().shoeSize(DEFAULT_SHOE_SIZE);
        return shoe;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Shoe createUpdatedEntity(EntityManager em) {
        Shoe shoe = new Shoe().shoeSize(UPDATED_SHOE_SIZE);
        return shoe;
    }

    @BeforeEach
    public void initTest() {
        shoe = createEntity(em);
    }

    @Test
    @Transactional
    void createShoe() throws Exception {
        int databaseSizeBeforeCreate = shoeRepository.findAll().size();
        // Create the Shoe
        ShoeDTO shoeDTO = shoeMapper.toDto(shoe);
        restShoeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(shoeDTO)))
            .andExpect(status().isCreated());

        // Validate the Shoe in the database
        List<Shoe> shoeList = shoeRepository.findAll();
        assertThat(shoeList).hasSize(databaseSizeBeforeCreate + 1);
        Shoe testShoe = shoeList.get(shoeList.size() - 1);
        assertThat(testShoe.getShoeSize()).isEqualTo(DEFAULT_SHOE_SIZE);

        // Validate the Shoe in Elasticsearch
        verify(mockShoeSearchRepository, times(1)).save(testShoe);
    }

    @Test
    @Transactional
    void createShoeWithExistingId() throws Exception {
        // Create the Shoe with an existing ID
        shoe.setId(1L);
        ShoeDTO shoeDTO = shoeMapper.toDto(shoe);

        int databaseSizeBeforeCreate = shoeRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restShoeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(shoeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Shoe in the database
        List<Shoe> shoeList = shoeRepository.findAll();
        assertThat(shoeList).hasSize(databaseSizeBeforeCreate);

        // Validate the Shoe in Elasticsearch
        verify(mockShoeSearchRepository, times(0)).save(shoe);
    }

    @Test
    @Transactional
    void getAllShoes() throws Exception {
        // Initialize the database
        shoeRepository.saveAndFlush(shoe);

        // Get all the shoeList
        restShoeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(shoe.getId().intValue())))
            .andExpect(jsonPath("$.[*].shoeSize").value(hasItem(DEFAULT_SHOE_SIZE)));
    }

    @Test
    @Transactional
    void getShoe() throws Exception {
        // Initialize the database
        shoeRepository.saveAndFlush(shoe);

        // Get the shoe
        restShoeMockMvc
            .perform(get(ENTITY_API_URL_ID, shoe.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(shoe.getId().intValue()))
            .andExpect(jsonPath("$.shoeSize").value(DEFAULT_SHOE_SIZE));
    }

    @Test
    @Transactional
    void getNonExistingShoe() throws Exception {
        // Get the shoe
        restShoeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewShoe() throws Exception {
        // Initialize the database
        shoeRepository.saveAndFlush(shoe);

        int databaseSizeBeforeUpdate = shoeRepository.findAll().size();

        // Update the shoe
        Shoe updatedShoe = shoeRepository.findById(shoe.getId()).get();
        // Disconnect from session so that the updates on updatedShoe are not directly saved in db
        em.detach(updatedShoe);
        updatedShoe.shoeSize(UPDATED_SHOE_SIZE);
        ShoeDTO shoeDTO = shoeMapper.toDto(updatedShoe);

        restShoeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, shoeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(shoeDTO))
            )
            .andExpect(status().isOk());

        // Validate the Shoe in the database
        List<Shoe> shoeList = shoeRepository.findAll();
        assertThat(shoeList).hasSize(databaseSizeBeforeUpdate);
        Shoe testShoe = shoeList.get(shoeList.size() - 1);
        assertThat(testShoe.getShoeSize()).isEqualTo(UPDATED_SHOE_SIZE);

        // Validate the Shoe in Elasticsearch
        verify(mockShoeSearchRepository).save(testShoe);
    }

    @Test
    @Transactional
    void putNonExistingShoe() throws Exception {
        int databaseSizeBeforeUpdate = shoeRepository.findAll().size();
        shoe.setId(count.incrementAndGet());

        // Create the Shoe
        ShoeDTO shoeDTO = shoeMapper.toDto(shoe);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restShoeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, shoeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(shoeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Shoe in the database
        List<Shoe> shoeList = shoeRepository.findAll();
        assertThat(shoeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Shoe in Elasticsearch
        verify(mockShoeSearchRepository, times(0)).save(shoe);
    }

    @Test
    @Transactional
    void putWithIdMismatchShoe() throws Exception {
        int databaseSizeBeforeUpdate = shoeRepository.findAll().size();
        shoe.setId(count.incrementAndGet());

        // Create the Shoe
        ShoeDTO shoeDTO = shoeMapper.toDto(shoe);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShoeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(shoeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Shoe in the database
        List<Shoe> shoeList = shoeRepository.findAll();
        assertThat(shoeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Shoe in Elasticsearch
        verify(mockShoeSearchRepository, times(0)).save(shoe);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamShoe() throws Exception {
        int databaseSizeBeforeUpdate = shoeRepository.findAll().size();
        shoe.setId(count.incrementAndGet());

        // Create the Shoe
        ShoeDTO shoeDTO = shoeMapper.toDto(shoe);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShoeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(shoeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Shoe in the database
        List<Shoe> shoeList = shoeRepository.findAll();
        assertThat(shoeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Shoe in Elasticsearch
        verify(mockShoeSearchRepository, times(0)).save(shoe);
    }

    @Test
    @Transactional
    void partialUpdateShoeWithPatch() throws Exception {
        // Initialize the database
        shoeRepository.saveAndFlush(shoe);

        int databaseSizeBeforeUpdate = shoeRepository.findAll().size();

        // Update the shoe using partial update
        Shoe partialUpdatedShoe = new Shoe();
        partialUpdatedShoe.setId(shoe.getId());

        partialUpdatedShoe.shoeSize(UPDATED_SHOE_SIZE);

        restShoeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedShoe.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedShoe))
            )
            .andExpect(status().isOk());

        // Validate the Shoe in the database
        List<Shoe> shoeList = shoeRepository.findAll();
        assertThat(shoeList).hasSize(databaseSizeBeforeUpdate);
        Shoe testShoe = shoeList.get(shoeList.size() - 1);
        assertThat(testShoe.getShoeSize()).isEqualTo(UPDATED_SHOE_SIZE);
    }

    @Test
    @Transactional
    void fullUpdateShoeWithPatch() throws Exception {
        // Initialize the database
        shoeRepository.saveAndFlush(shoe);

        int databaseSizeBeforeUpdate = shoeRepository.findAll().size();

        // Update the shoe using partial update
        Shoe partialUpdatedShoe = new Shoe();
        partialUpdatedShoe.setId(shoe.getId());

        partialUpdatedShoe.shoeSize(UPDATED_SHOE_SIZE);

        restShoeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedShoe.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedShoe))
            )
            .andExpect(status().isOk());

        // Validate the Shoe in the database
        List<Shoe> shoeList = shoeRepository.findAll();
        assertThat(shoeList).hasSize(databaseSizeBeforeUpdate);
        Shoe testShoe = shoeList.get(shoeList.size() - 1);
        assertThat(testShoe.getShoeSize()).isEqualTo(UPDATED_SHOE_SIZE);
    }

    @Test
    @Transactional
    void patchNonExistingShoe() throws Exception {
        int databaseSizeBeforeUpdate = shoeRepository.findAll().size();
        shoe.setId(count.incrementAndGet());

        // Create the Shoe
        ShoeDTO shoeDTO = shoeMapper.toDto(shoe);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restShoeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, shoeDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(shoeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Shoe in the database
        List<Shoe> shoeList = shoeRepository.findAll();
        assertThat(shoeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Shoe in Elasticsearch
        verify(mockShoeSearchRepository, times(0)).save(shoe);
    }

    @Test
    @Transactional
    void patchWithIdMismatchShoe() throws Exception {
        int databaseSizeBeforeUpdate = shoeRepository.findAll().size();
        shoe.setId(count.incrementAndGet());

        // Create the Shoe
        ShoeDTO shoeDTO = shoeMapper.toDto(shoe);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShoeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(shoeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Shoe in the database
        List<Shoe> shoeList = shoeRepository.findAll();
        assertThat(shoeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Shoe in Elasticsearch
        verify(mockShoeSearchRepository, times(0)).save(shoe);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamShoe() throws Exception {
        int databaseSizeBeforeUpdate = shoeRepository.findAll().size();
        shoe.setId(count.incrementAndGet());

        // Create the Shoe
        ShoeDTO shoeDTO = shoeMapper.toDto(shoe);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShoeMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(shoeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Shoe in the database
        List<Shoe> shoeList = shoeRepository.findAll();
        assertThat(shoeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Shoe in Elasticsearch
        verify(mockShoeSearchRepository, times(0)).save(shoe);
    }

    @Test
    @Transactional
    void deleteShoe() throws Exception {
        // Initialize the database
        shoeRepository.saveAndFlush(shoe);

        int databaseSizeBeforeDelete = shoeRepository.findAll().size();

        // Delete the shoe
        restShoeMockMvc
            .perform(delete(ENTITY_API_URL_ID, shoe.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Shoe> shoeList = shoeRepository.findAll();
        assertThat(shoeList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Shoe in Elasticsearch
        verify(mockShoeSearchRepository, times(1)).deleteById(shoe.getId());
    }

    @Test
    @Transactional
    void searchShoe() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        shoeRepository.saveAndFlush(shoe);
        when(mockShoeSearchRepository.search(queryStringQuery("id:" + shoe.getId()))).thenReturn(Collections.singletonList(shoe));

        // Search the shoe
        restShoeMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + shoe.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(shoe.getId().intValue())))
            .andExpect(jsonPath("$.[*].shoeSize").value(hasItem(DEFAULT_SHOE_SIZE)));
    }
}
