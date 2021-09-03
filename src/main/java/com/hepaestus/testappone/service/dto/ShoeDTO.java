package com.hepaestus.testappone.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.hepaestus.testappone.domain.Shoe} entity.
 */
public class ShoeDTO implements Serializable {

    private Long id;

    @Min(value = 4)
    @Max(value = 18)
    private Integer shoeSize;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getShoeSize() {
        return shoeSize;
    }

    public void setShoeSize(Integer shoeSize) {
        this.shoeSize = shoeSize;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ShoeDTO)) {
            return false;
        }

        ShoeDTO shoeDTO = (ShoeDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, shoeDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ShoeDTO{" +
            "id=" + getId() +
            ", shoeSize=" + getShoeSize() +
            "}";
    }
}
