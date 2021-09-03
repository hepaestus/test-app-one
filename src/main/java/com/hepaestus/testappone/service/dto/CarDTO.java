package com.hepaestus.testappone.service.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.hepaestus.testappone.domain.Car} entity.
 */
public class CarDTO implements Serializable {

    private Long id;

    private String make;

    private String model;

    private LocalDate year;

    private DriverDTO driver;

    private Set<PersonDTO> passengers = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public LocalDate getYear() {
        return year;
    }

    public void setYear(LocalDate year) {
        this.year = year;
    }

    public DriverDTO getDriver() {
        return driver;
    }

    public void setDriver(DriverDTO driver) {
        this.driver = driver;
    }

    public Set<PersonDTO> getPassengers() {
        return passengers;
    }

    public void setPassengers(Set<PersonDTO> passengers) {
        this.passengers = passengers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CarDTO)) {
            return false;
        }

        CarDTO carDTO = (CarDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, carDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CarDTO{" +
            "id=" + getId() +
            ", make='" + getMake() + "'" +
            ", model='" + getModel() + "'" +
            ", year='" + getYear() + "'" +
            ", driver=" + getDriver() +
            ", passengers=" + getPassengers() +
            "}";
    }
}
