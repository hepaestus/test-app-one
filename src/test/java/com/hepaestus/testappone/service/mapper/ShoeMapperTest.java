package com.hepaestus.testappone.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ShoeMapperTest {

    private ShoeMapper shoeMapper;

    @BeforeEach
    public void setUp() {
        shoeMapper = new ShoeMapperImpl();
    }
}
