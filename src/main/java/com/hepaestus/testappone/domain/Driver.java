package com.hepaestus.testappone.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * A Driver.
 */
@Entity
@Table(name = "driver")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "driver")
public class Driver implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Pattern(regexp = "^[A-Z][a-z]{6}[0-9]{1}")
    @Column(name = "license_number", nullable = false)
    private String licenseNumber;

    @JsonIgnoreProperties(value = { "user", "shoes", "driver", "cars" }, allowSetters = true)
    @OneToOne(optional = false)
    @NotNull
    @JoinColumn(unique = true)
    private Person person;

    @OneToMany(mappedBy = "driver", fetch = FetchType.EAGER)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "driver", "passengers" }, allowSetters = true, ignoreUnknown = true)
    private Set<Car> cars = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Driver id(Long id) {
        this.id = id;
        return this;
    }

    public String getLicenseNumber() {
        return this.licenseNumber;
    }

    public Driver licenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
        return this;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public Person getPerson() {
        return this.person;
    }

    public Driver person(Person person) {
        this.setPerson(person);
        return this;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Set<Car> getCars() {
        return this.cars;
    }

    public Driver cars(Set<Car> cars) {
        this.setCars(cars);
        return this;
    }

    public Driver addCar(Car car) {
        this.cars.add(car);
        car.setDriver(this);
        return this;
    }

    public Driver removeCar(Car car) {
        this.cars.remove(car);
        car.setDriver(null);
        return this;
    }

    public void setCars(Set<Car> cars) {
        if (this.cars != null) {
            this.cars.forEach(i -> i.setDriver(null));
        }
        if (cars != null) {
            cars.forEach(i -> i.setDriver(this));
        }
        this.cars = cars;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and
    // setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Driver)) {
            return false;
        }
        return id != null && id.equals(((Driver) o).id);
    }

    @Override
    public int hashCode() {
        // see
        // https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Driver{" + "id=" + getId() + ", licenseNumber='" + getLicenseNumber() + "'" + "}";
    }
}
