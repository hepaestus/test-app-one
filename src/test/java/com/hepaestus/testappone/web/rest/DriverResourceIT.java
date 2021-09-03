package com.hepaestus.testappone.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.hepaestus.testappone.IntegrationTest;
import com.hepaestus.testappone.domain.Car;
import com.hepaestus.testappone.domain.Driver;
import com.hepaestus.testappone.domain.Person;
import com.hepaestus.testappone.repository.DriverRepository;
import com.hepaestus.testappone.repository.search.DriverSearchRepository;
import com.hepaestus.testappone.service.dto.DriverDTO;
import com.hepaestus.testappone.service.mapper.DriverMapper;
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
 * Integration tests for the {@link DriverResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class DriverResourceIT {

    private static final String DEFAULT_LICENSE_NUMBER = "Sorvfsj8";
    private static final String UPDATED_LICENSE_NUMBER = "Kaqyila1";

    private static final String ENTITY_API_URL = "/api/drivers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/drivers";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private DriverMapper driverMapper;

    /**
     * This repository is mocked in the com.hepaestus.testappone.repository.search test package.
     *
     * @see com.hepaestus.testappone.repository.search.DriverSearchRepositoryMockConfiguration
     */
    @Autowired
    private DriverSearchRepository mockDriverSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDriverMockMvc;

    private Driver driver;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Driver createEntity(EntityManager em) {
        Driver driver = new Driver().licenseNumber(DEFAULT_LICENSE_NUMBER);
        // Add required entity
        Person person;
        if (TestUtil.findAll(em, Person.class).isEmpty()) {
            person = PersonResourceIT.createEntity(em);
            em.persist(person);
            em.flush();
        } else {
            person = TestUtil.findAll(em, Person.class).get(0);
        }
        driver.setPerson(person);
        // Add required entity
        Car car;
        if (TestUtil.findAll(em, Car.class).isEmpty()) {
            car = CarResourceIT.createEntity(em);
            em.persist(car);
            em.flush();
        } else {
            car = TestUtil.findAll(em, Car.class).get(0);
        }
        driver.getCars().add(car);
        return driver;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Driver createUpdatedEntity(EntityManager em) {
        Driver driver = new Driver().licenseNumber(UPDATED_LICENSE_NUMBER);
        // Add required entity
        Person person;
        if (TestUtil.findAll(em, Person.class).isEmpty()) {
            person = PersonResourceIT.createUpdatedEntity(em);
            em.persist(person);
            em.flush();
        } else {
            person = TestUtil.findAll(em, Person.class).get(0);
        }
        driver.setPerson(person);
        // Add required entity
        Car car;
        if (TestUtil.findAll(em, Car.class).isEmpty()) {
            car = CarResourceIT.createUpdatedEntity(em);
            em.persist(car);
            em.flush();
        } else {
            car = TestUtil.findAll(em, Car.class).get(0);
        }
        driver.getCars().add(car);
        return driver;
    }

    @BeforeEach
    public void initTest() {
        driver = createEntity(em);
    }

    @Test
    @Transactional
    void createDriver() throws Exception {
        int databaseSizeBeforeCreate = driverRepository.findAll().size();
        // Create the Driver
        DriverDTO driverDTO = driverMapper.toDto(driver);
        restDriverMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(driverDTO)))
            .andExpect(status().isCreated());

        // Validate the Driver in the database
        List<Driver> driverList = driverRepository.findAll();
        assertThat(driverList).hasSize(databaseSizeBeforeCreate + 1);
        Driver testDriver = driverList.get(driverList.size() - 1);
        assertThat(testDriver.getLicenseNumber()).isEqualTo(DEFAULT_LICENSE_NUMBER);

        // Validate the Driver in Elasticsearch
        verify(mockDriverSearchRepository, times(1)).save(testDriver);
    }

    @Test
    @Transactional
    void createDriverWithExistingId() throws Exception {
        // Create the Driver with an existing ID
        driver.setId(1L);
        DriverDTO driverDTO = driverMapper.toDto(driver);

        int databaseSizeBeforeCreate = driverRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restDriverMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(driverDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Driver in the database
        List<Driver> driverList = driverRepository.findAll();
        assertThat(driverList).hasSize(databaseSizeBeforeCreate);

        // Validate the Driver in Elasticsearch
        verify(mockDriverSearchRepository, times(0)).save(driver);
    }

    @Test
    @Transactional
    void checkLicenseNumberIsRequired() throws Exception {
        int databaseSizeBeforeTest = driverRepository.findAll().size();
        // set the field null
        driver.setLicenseNumber(null);

        // Create the Driver, which fails.
        DriverDTO driverDTO = driverMapper.toDto(driver);

        restDriverMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(driverDTO)))
            .andExpect(status().isBadRequest());

        List<Driver> driverList = driverRepository.findAll();
        assertThat(driverList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllDrivers() throws Exception {
        // Initialize the database
        driverRepository.saveAndFlush(driver);

        // Get all the driverList
        restDriverMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(driver.getId().intValue())))
            .andExpect(jsonPath("$.[*].licenseNumber").value(hasItem(DEFAULT_LICENSE_NUMBER)));
    }

    @Test
    @Transactional
    void getDriver() throws Exception {
        // Initialize the database
        driverRepository.saveAndFlush(driver);

        // Get the driver
        restDriverMockMvc
            .perform(get(ENTITY_API_URL_ID, driver.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(driver.getId().intValue()))
            .andExpect(jsonPath("$.licenseNumber").value(DEFAULT_LICENSE_NUMBER));
    }

    @Test
    @Transactional
    void getNonExistingDriver() throws Exception {
        // Get the driver
        restDriverMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewDriver() throws Exception {
        // Initialize the database
        driverRepository.saveAndFlush(driver);

        int databaseSizeBeforeUpdate = driverRepository.findAll().size();

        // Update the driver
        Driver updatedDriver = driverRepository.findById(driver.getId()).get();
        // Disconnect from session so that the updates on updatedDriver are not directly saved in db
        em.detach(updatedDriver);
        updatedDriver.licenseNumber(UPDATED_LICENSE_NUMBER);
        DriverDTO driverDTO = driverMapper.toDto(updatedDriver);

        restDriverMockMvc
            .perform(
                put(ENTITY_API_URL_ID, driverDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(driverDTO))
            )
            .andExpect(status().isOk());

        // Validate the Driver in the database
        List<Driver> driverList = driverRepository.findAll();
        assertThat(driverList).hasSize(databaseSizeBeforeUpdate);
        Driver testDriver = driverList.get(driverList.size() - 1);
        assertThat(testDriver.getLicenseNumber()).isEqualTo(UPDATED_LICENSE_NUMBER);

        // Validate the Driver in Elasticsearch
        verify(mockDriverSearchRepository).save(testDriver);
    }

    @Test
    @Transactional
    void putNonExistingDriver() throws Exception {
        int databaseSizeBeforeUpdate = driverRepository.findAll().size();
        driver.setId(count.incrementAndGet());

        // Create the Driver
        DriverDTO driverDTO = driverMapper.toDto(driver);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDriverMockMvc
            .perform(
                put(ENTITY_API_URL_ID, driverDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(driverDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Driver in the database
        List<Driver> driverList = driverRepository.findAll();
        assertThat(driverList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Driver in Elasticsearch
        verify(mockDriverSearchRepository, times(0)).save(driver);
    }

    @Test
    @Transactional
    void putWithIdMismatchDriver() throws Exception {
        int databaseSizeBeforeUpdate = driverRepository.findAll().size();
        driver.setId(count.incrementAndGet());

        // Create the Driver
        DriverDTO driverDTO = driverMapper.toDto(driver);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDriverMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(driverDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Driver in the database
        List<Driver> driverList = driverRepository.findAll();
        assertThat(driverList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Driver in Elasticsearch
        verify(mockDriverSearchRepository, times(0)).save(driver);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDriver() throws Exception {
        int databaseSizeBeforeUpdate = driverRepository.findAll().size();
        driver.setId(count.incrementAndGet());

        // Create the Driver
        DriverDTO driverDTO = driverMapper.toDto(driver);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDriverMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(driverDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Driver in the database
        List<Driver> driverList = driverRepository.findAll();
        assertThat(driverList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Driver in Elasticsearch
        verify(mockDriverSearchRepository, times(0)).save(driver);
    }

    @Test
    @Transactional
    void partialUpdateDriverWithPatch() throws Exception {
        // Initialize the database
        driverRepository.saveAndFlush(driver);

        int databaseSizeBeforeUpdate = driverRepository.findAll().size();

        // Update the driver using partial update
        Driver partialUpdatedDriver = new Driver();
        partialUpdatedDriver.setId(driver.getId());

        restDriverMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDriver.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDriver))
            )
            .andExpect(status().isOk());

        // Validate the Driver in the database
        List<Driver> driverList = driverRepository.findAll();
        assertThat(driverList).hasSize(databaseSizeBeforeUpdate);
        Driver testDriver = driverList.get(driverList.size() - 1);
        assertThat(testDriver.getLicenseNumber()).isEqualTo(DEFAULT_LICENSE_NUMBER);
    }

    @Test
    @Transactional
    void fullUpdateDriverWithPatch() throws Exception {
        // Initialize the database
        driverRepository.saveAndFlush(driver);

        int databaseSizeBeforeUpdate = driverRepository.findAll().size();

        // Update the driver using partial update
        Driver partialUpdatedDriver = new Driver();
        partialUpdatedDriver.setId(driver.getId());

        partialUpdatedDriver.licenseNumber(UPDATED_LICENSE_NUMBER);

        restDriverMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDriver.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDriver))
            )
            .andExpect(status().isOk());

        // Validate the Driver in the database
        List<Driver> driverList = driverRepository.findAll();
        assertThat(driverList).hasSize(databaseSizeBeforeUpdate);
        Driver testDriver = driverList.get(driverList.size() - 1);
        assertThat(testDriver.getLicenseNumber()).isEqualTo(UPDATED_LICENSE_NUMBER);
    }

    @Test
    @Transactional
    void patchNonExistingDriver() throws Exception {
        int databaseSizeBeforeUpdate = driverRepository.findAll().size();
        driver.setId(count.incrementAndGet());

        // Create the Driver
        DriverDTO driverDTO = driverMapper.toDto(driver);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDriverMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, driverDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(driverDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Driver in the database
        List<Driver> driverList = driverRepository.findAll();
        assertThat(driverList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Driver in Elasticsearch
        verify(mockDriverSearchRepository, times(0)).save(driver);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDriver() throws Exception {
        int databaseSizeBeforeUpdate = driverRepository.findAll().size();
        driver.setId(count.incrementAndGet());

        // Create the Driver
        DriverDTO driverDTO = driverMapper.toDto(driver);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDriverMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(driverDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Driver in the database
        List<Driver> driverList = driverRepository.findAll();
        assertThat(driverList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Driver in Elasticsearch
        verify(mockDriverSearchRepository, times(0)).save(driver);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDriver() throws Exception {
        int databaseSizeBeforeUpdate = driverRepository.findAll().size();
        driver.setId(count.incrementAndGet());

        // Create the Driver
        DriverDTO driverDTO = driverMapper.toDto(driver);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDriverMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(driverDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Driver in the database
        List<Driver> driverList = driverRepository.findAll();
        assertThat(driverList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Driver in Elasticsearch
        verify(mockDriverSearchRepository, times(0)).save(driver);
    }

    @Test
    @Transactional
    void deleteDriver() throws Exception {
        // Initialize the database
        driverRepository.saveAndFlush(driver);

        int databaseSizeBeforeDelete = driverRepository.findAll().size();

        // Delete the driver
        restDriverMockMvc
            .perform(delete(ENTITY_API_URL_ID, driver.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Driver> driverList = driverRepository.findAll();
        assertThat(driverList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Driver in Elasticsearch
        verify(mockDriverSearchRepository, times(1)).deleteById(driver.getId());
    }

    @Test
    @Transactional
    void searchDriver() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        driverRepository.saveAndFlush(driver);
        when(mockDriverSearchRepository.search(queryStringQuery("id:" + driver.getId()))).thenReturn(Collections.singletonList(driver));

        // Search the driver
        restDriverMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + driver.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(driver.getId().intValue())))
            .andExpect(jsonPath("$.[*].licenseNumber").value(hasItem(DEFAULT_LICENSE_NUMBER)));
    }
}
