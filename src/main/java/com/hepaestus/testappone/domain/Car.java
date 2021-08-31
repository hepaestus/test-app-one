package com.hepaestus.testappone.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Car.
 */
@Entity
@Table(name = "car")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Car implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "make")
    private String make;

    @Column(name = "model")
    private String model;

    @JsonIgnoreProperties(value = { "person" }, allowSetters = true)
    @OneToOne
    @JoinColumn(unique = true)
    private Driver driver;

    @OneToMany(mappedBy = "car")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "car" }, allowSetters = true)
    private Set<Person> passengers = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Car id(Long id) {
        this.id = id;
        return this;
    }

    public String getMake() {
        return this.make;
    }

    public Car make(String make) {
        this.make = make;
        return this;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return this.model;
    }

    public Car model(String model) {
        this.model = model;
        return this;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Driver getDriver() {
        return this.driver;
    }

    public Car driver(Driver driver) {
        this.setDriver(driver);
        return this;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public Set<Person> getPassengers() {
        return this.passengers;
    }

    public Car passengers(Set<Person> people) {
        this.setPassengers(people);
        return this;
    }

    public Car addPassengers(Person person) {
        this.passengers.add(person);
        person.setCar(this);
        return this;
    }

    public Car removePassengers(Person person) {
        this.passengers.remove(person);
        person.setCar(null);
        return this;
    }

    public void setPassengers(Set<Person> people) {
        if (this.passengers != null) {
            this.passengers.forEach(i -> i.setCar(null));
        }
        if (people != null) {
            people.forEach(i -> i.setCar(this));
        }
        this.passengers = people;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Car)) {
            return false;
        }
        return id != null && id.equals(((Car) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Car{" +
            "id=" + getId() +
            ", make='" + getMake() + "'" +
            ", model='" + getModel() + "'" +
            "}";
    }
}
