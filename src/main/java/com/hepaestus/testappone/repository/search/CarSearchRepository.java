package com.hepaestus.testappone.repository.search;

import com.hepaestus.testappone.domain.Car;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Car} entity.
 */
public interface CarSearchRepository extends ElasticsearchRepository<Car, Long> {}
