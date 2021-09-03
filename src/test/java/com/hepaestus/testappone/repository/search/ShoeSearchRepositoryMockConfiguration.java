package com.hepaestus.testappone.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link ShoeSearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class ShoeSearchRepositoryMockConfiguration {

    @MockBean
    private ShoeSearchRepository mockShoeSearchRepository;
}
