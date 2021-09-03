package com.hepaestus.testappone.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.hepaestus.testappone.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ShoeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Shoe.class);
        Shoe shoe1 = new Shoe();
        shoe1.setId(1L);
        Shoe shoe2 = new Shoe();
        shoe2.setId(shoe1.getId());
        assertThat(shoe1).isEqualTo(shoe2);
        shoe2.setId(2L);
        assertThat(shoe1).isNotEqualTo(shoe2);
        shoe1.setId(null);
        assertThat(shoe1).isNotEqualTo(shoe2);
    }
}
