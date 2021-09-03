package com.hepaestus.testappone.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link PersonSearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class PersonSearchRepositoryMockConfiguration {

    @MockBean
    private PersonSearchRepository mockPersonSearchRepository;
}
