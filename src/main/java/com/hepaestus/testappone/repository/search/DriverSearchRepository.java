package com.hepaestus.testappone.repository.search;

import com.hepaestus.testappone.domain.Driver;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Driver} entity.
 */
public interface DriverSearchRepository extends ElasticsearchRepository<Driver, Long> {}
