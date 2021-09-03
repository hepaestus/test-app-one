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
import com.hepaestus.testappone.repository.CarRepository;
import com.hepaestus.testappone.repository.search.CarSearchRepository;
import com.hepaestus.testappone.service.CarService;
import com.hepaestus.testappone.service.dto.CarDTO;
import com.hepaestus.testappone.service.mapper.CarMapper;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link CarResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class CarResourceIT {

    private static final String DEFAULT_MAKE = "AAAAAAAAAA";
    private static final String UPDATED_MAKE = "BBBBBBBBBB";

    private static final String DEFAULT_MODEL = "AAAAAAAAAA";
    private static final String UPDATED_MODEL = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_YEAR = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_YEAR = LocalDate.now(ZoneId.systemDefault());

    private static final String ENTITY_API_URL = "/api/cars";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/cars";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CarRepository carRepository;

    @Mock
    private CarRepository carRepositoryMock;

    @Autowired
    private CarMapper carMapper;

    @Mock
    private CarService carServiceMock;

    /**
     * This repository is mocked in the com.hepaestus.testappone.repository.search test package.
     *
     * @see com.hepaestus.testappone.repository.search.CarSearchRepositoryMockConfiguration
     */
    @Autowired
    private CarSearchRepository mockCarSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCarMockMvc;

    private Car car;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Car createEntity(EntityManager em) {
        Car car = new Car().make(DEFAULT_MAKE).model(DEFAULT_MODEL).year(DEFAULT_YEAR);
        // Add required entity
        Driver driver;
        if (TestUtil.findAll(em, Driver.class).isEmpty()) {
            driver = DriverResourceIT.createEntity(em);
            em.persist(driver);
            em.flush();
        } else {
            driver = TestUtil.findAll(em, Driver.class).get(0);
        }
        car.setDriver(driver);
        return car;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Car createUpdatedEntity(EntityManager em) {
        Car car = new Car().make(UPDATED_MAKE).model(UPDATED_MODEL).year(UPDATED_YEAR);
        // Add required entity
        Driver driver;
        if (TestUtil.findAll(em, Driver.class).isEmpty()) {
            driver = DriverResourceIT.createUpdatedEntity(em);
            em.persist(driver);
            em.flush();
        } else {
            driver = TestUtil.findAll(em, Driver.class).get(0);
        }
        car.setDriver(driver);
        return car;
    }

    @BeforeEach
    public void initTest() {
        car = createEntity(em);
    }

    @Test
    @Transactional
    void createCar() throws Exception {
        int databaseSizeBeforeCreate = carRepository.findAll().size();
        // Create the Car
        CarDTO carDTO = carMapper.toDto(car);
        restCarMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(carDTO)))
            .andExpect(status().isCreated());

        // Validate the Car in the database
        List<Car> carList = carRepository.findAll();
        assertThat(carList).hasSize(databaseSizeBeforeCreate + 1);
        Car testCar = carList.get(carList.size() - 1);
        assertThat(testCar.getMake()).isEqualTo(DEFAULT_MAKE);
        assertThat(testCar.getModel()).isEqualTo(DEFAULT_MODEL);
        assertThat(testCar.getYear()).isEqualTo(DEFAULT_YEAR);

        // Validate the Car in Elasticsearch
        verify(mockCarSearchRepository, times(1)).save(testCar);
    }

    @Test
    @Transactional
    void createCarWithExistingId() throws Exception {
        // Create the Car with an existing ID
        car.setId(1L);
        CarDTO carDTO = carMapper.toDto(car);

        int databaseSizeBeforeCreate = carRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCarMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(carDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Car in the database
        List<Car> carList = carRepository.findAll();
        assertThat(carList).hasSize(databaseSizeBeforeCreate);

        // Validate the Car in Elasticsearch
        verify(mockCarSearchRepository, times(0)).save(car);
    }

    @Test
    @Transactional
    void getAllCars() throws Exception {
        // Initialize the database
        carRepository.saveAndFlush(car);

        // Get all the carList
        restCarMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(car.getId().intValue())))
            .andExpect(jsonPath("$.[*].make").value(hasItem(DEFAULT_MAKE)))
            .andExpect(jsonPath("$.[*].model").value(hasItem(DEFAULT_MODEL)))
            .andExpect(jsonPath("$.[*].year").value(hasItem(DEFAULT_YEAR.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCarsWithEagerRelationshipsIsEnabled() throws Exception {
        when(carServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCarMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(carServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCarsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(carServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCarMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(carServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    void getCar() throws Exception {
        // Initialize the database
        carRepository.saveAndFlush(car);

        // Get the car
        restCarMockMvc
            .perform(get(ENTITY_API_URL_ID, car.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(car.getId().intValue()))
            .andExpect(jsonPath("$.make").value(DEFAULT_MAKE))
            .andExpect(jsonPath("$.model").value(DEFAULT_MODEL))
            .andExpect(jsonPath("$.year").value(DEFAULT_YEAR.toString()));
    }

    @Test
    @Transactional
    void getNonExistingCar() throws Exception {
        // Get the car
        restCarMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewCar() throws Exception {
        // Initialize the database
        carRepository.saveAndFlush(car);

        int databaseSizeBeforeUpdate = carRepository.findAll().size();

        // Update the car
        Car updatedCar = carRepository.findById(car.getId()).get();
        // Disconnect from session so that the updates on updatedCar are not directly saved in db
        em.detach(updatedCar);
        updatedCar.make(UPDATED_MAKE).model(UPDATED_MODEL).year(UPDATED_YEAR);
        CarDTO carDTO = carMapper.toDto(updatedCar);

        restCarMockMvc
            .perform(
                put(ENTITY_API_URL_ID, carDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(carDTO))
            )
            .andExpect(status().isOk());

        // Validate the Car in the database
        List<Car> carList = carRepository.findAll();
        assertThat(carList).hasSize(databaseSizeBeforeUpdate);
        Car testCar = carList.get(carList.size() - 1);
        assertThat(testCar.getMake()).isEqualTo(UPDATED_MAKE);
        assertThat(testCar.getModel()).isEqualTo(UPDATED_MODEL);
        assertThat(testCar.getYear()).isEqualTo(UPDATED_YEAR);

        // Validate the Car in Elasticsearch
        verify(mockCarSearchRepository).save(testCar);
    }

    @Test
    @Transactional
    void putNonExistingCar() throws Exception {
        int databaseSizeBeforeUpdate = carRepository.findAll().size();
        car.setId(count.incrementAndGet());

        // Create the Car
        CarDTO carDTO = carMapper.toDto(car);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCarMockMvc
            .perform(
                put(ENTITY_API_URL_ID, carDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(carDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Car in the database
        List<Car> carList = carRepository.findAll();
        assertThat(carList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Car in Elasticsearch
        verify(mockCarSearchRepository, times(0)).save(car);
    }

    @Test
    @Transactional
    void putWithIdMismatchCar() throws Exception {
        int databaseSizeBeforeUpdate = carRepository.findAll().size();
        car.setId(count.incrementAndGet());

        // Create the Car
        CarDTO carDTO = carMapper.toDto(car);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCarMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(carDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Car in the database
        List<Car> carList = carRepository.findAll();
        assertThat(carList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Car in Elasticsearch
        verify(mockCarSearchRepository, times(0)).save(car);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCar() throws Exception {
        int databaseSizeBeforeUpdate = carRepository.findAll().size();
        car.setId(count.incrementAndGet());

        // Create the Car
        CarDTO carDTO = carMapper.toDto(car);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCarMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(carDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Car in the database
        List<Car> carList = carRepository.findAll();
        assertThat(carList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Car in Elasticsearch
        verify(mockCarSearchRepository, times(0)).save(car);
    }

    @Test
    @Transactional
    void partialUpdateCarWithPatch() throws Exception {
        // Initialize the database
        carRepository.saveAndFlush(car);

        int databaseSizeBeforeUpdate = carRepository.findAll().size();

        // Update the car using partial update
        Car partialUpdatedCar = new Car();
        partialUpdatedCar.setId(car.getId());

        partialUpdatedCar.make(UPDATED_MAKE).model(UPDATED_MODEL).year(UPDATED_YEAR);

        restCarMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCar.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCar))
            )
            .andExpect(status().isOk());

        // Validate the Car in the database
        List<Car> carList = carRepository.findAll();
        assertThat(carList).hasSize(databaseSizeBeforeUpdate);
        Car testCar = carList.get(carList.size() - 1);
        assertThat(testCar.getMake()).isEqualTo(UPDATED_MAKE);
        assertThat(testCar.getModel()).isEqualTo(UPDATED_MODEL);
        assertThat(testCar.getYear()).isEqualTo(UPDATED_YEAR);
    }

    @Test
    @Transactional
    void fullUpdateCarWithPatch() throws Exception {
        // Initialize the database
        carRepository.saveAndFlush(car);

        int databaseSizeBeforeUpdate = carRepository.findAll().size();

        // Update the car using partial update
        Car partialUpdatedCar = new Car();
        partialUpdatedCar.setId(car.getId());

        partialUpdatedCar.make(UPDATED_MAKE).model(UPDATED_MODEL).year(UPDATED_YEAR);

        restCarMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCar.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCar))
            )
            .andExpect(status().isOk());

        // Validate the Car in the database
        List<Car> carList = carRepository.findAll();
        assertThat(carList).hasSize(databaseSizeBeforeUpdate);
        Car testCar = carList.get(carList.size() - 1);
        assertThat(testCar.getMake()).isEqualTo(UPDATED_MAKE);
        assertThat(testCar.getModel()).isEqualTo(UPDATED_MODEL);
        assertThat(testCar.getYear()).isEqualTo(UPDATED_YEAR);
    }

    @Test
    @Transactional
    void patchNonExistingCar() throws Exception {
        int databaseSizeBeforeUpdate = carRepository.findAll().size();
        car.setId(count.incrementAndGet());

        // Create the Car
        CarDTO carDTO = carMapper.toDto(car);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCarMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, carDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(carDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Car in the database
        List<Car> carList = carRepository.findAll();
        assertThat(carList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Car in Elasticsearch
        verify(mockCarSearchRepository, times(0)).save(car);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCar() throws Exception {
        int databaseSizeBeforeUpdate = carRepository.findAll().size();
        car.setId(count.incrementAndGet());

        // Create the Car
        CarDTO carDTO = carMapper.toDto(car);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCarMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(carDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Car in the database
        List<Car> carList = carRepository.findAll();
        assertThat(carList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Car in Elasticsearch
        verify(mockCarSearchRepository, times(0)).save(car);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCar() throws Exception {
        int databaseSizeBeforeUpdate = carRepository.findAll().size();
        car.setId(count.incrementAndGet());

        // Create the Car
        CarDTO carDTO = carMapper.toDto(car);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCarMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(carDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Car in the database
        List<Car> carList = carRepository.findAll();
        assertThat(carList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Car in Elasticsearch
        verify(mockCarSearchRepository, times(0)).save(car);
    }

    @Test
    @Transactional
    void deleteCar() throws Exception {
        // Initialize the database
        carRepository.saveAndFlush(car);

        int databaseSizeBeforeDelete = carRepository.findAll().size();

        // Delete the car
        restCarMockMvc.perform(delete(ENTITY_API_URL_ID, car.getId()).accept(MediaType.APPLICATION_JSON)).andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Car> carList = carRepository.findAll();
        assertThat(carList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Car in Elasticsearch
        verify(mockCarSearchRepository, times(1)).deleteById(car.getId());
    }

    @Test
    @Transactional
    void searchCar() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        carRepository.saveAndFlush(car);
        when(mockCarSearchRepository.search(queryStringQuery("id:" + car.getId()))).thenReturn(Collections.singletonList(car));

        // Search the car
        restCarMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + car.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(car.getId().intValue())))
            .andExpect(jsonPath("$.[*].make").value(hasItem(DEFAULT_MAKE)))
            .andExpect(jsonPath("$.[*].model").value(hasItem(DEFAULT_MODEL)))
            .andExpect(jsonPath("$.[*].year").value(hasItem(DEFAULT_YEAR.toString())));
    }
}
