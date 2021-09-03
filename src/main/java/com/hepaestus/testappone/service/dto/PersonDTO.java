package com.hepaestus.testappone.service.dto;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link com.hepaestus.testappone.domain.Person} entity.
 */
public class PersonDTO implements Serializable {

    private Long id;

    private String name;

    private UserDTO user;

    private Set<ShoeDTO> shoes = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public Set<ShoeDTO> getShoes() {
        return shoes;
    }

    public void setShoes(Set<ShoeDTO> shoes) {
        this.shoes = shoes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PersonDTO)) {
            return false;
        }

        PersonDTO personDTO = (PersonDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, personDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PersonDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", user=" + getUser() +
            ", shoes=" + getShoes() +
            "}";
    }
}
