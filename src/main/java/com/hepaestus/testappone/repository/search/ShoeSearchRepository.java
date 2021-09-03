package com.hepaestus.testappone.repository.search;

import com.hepaestus.testappone.domain.Shoe;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Shoe} entity.
 */
public interface ShoeSearchRepository extends ElasticsearchRepository<Shoe, Long> {}
