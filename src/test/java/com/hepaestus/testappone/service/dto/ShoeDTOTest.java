package com.hepaestus.testappone.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.hepaestus.testappone.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ShoeDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ShoeDTO.class);
        ShoeDTO shoeDTO1 = new ShoeDTO();
        shoeDTO1.setId(1L);
        ShoeDTO shoeDTO2 = new ShoeDTO();
        assertThat(shoeDTO1).isNotEqualTo(shoeDTO2);
        shoeDTO2.setId(shoeDTO1.getId());
        assertThat(shoeDTO1).isEqualTo(shoeDTO2);
        shoeDTO2.setId(2L);
        assertThat(shoeDTO1).isNotEqualTo(shoeDTO2);
        shoeDTO1.setId(null);
        assertThat(shoeDTO1).isNotEqualTo(shoeDTO2);
    }
}
