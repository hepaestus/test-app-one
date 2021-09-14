package com.hepaestus.testappone.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * A Person.
 */
@Entity
@Table(name = "person")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "person")
public class Person implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @OneToOne
    @JoinColumn(unique = true)
    private User user;

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JoinTable(name = "rel_person__shoe", joinColumns = @JoinColumn(name = "person_id"), inverseJoinColumns = @JoinColumn(name = "shoe_id"))
    @JsonIgnoreProperties(value = { "people" }, allowSetters = true)
    private Set<Shoe> shoes = new HashSet<>();

    @JsonIgnoreProperties(value = { "person", "cars" }, allowSetters = true)
    @OneToOne(mappedBy = "person")
    private Driver driver;

    @ManyToMany(mappedBy = "passengers")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "driver", "passengers" }, allowSetters = true)
    private Set<Car> cars = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Person id(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        if (this.name == null) {
            this.name = this.user.getFirstName() + " " + this.user.getLastName();
        }
        return this.name;
    }

    public Person name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getUser() {
        return this.user;
    }

    public Person user(User user) {
        this.setUser(user);
        return this;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set<Shoe> getShoes() {
        return this.shoes;
    }

    public Person shoes(Set<Shoe> shoes) {
        this.setShoes(shoes);
        return this;
    }

    public Person addShoe(Shoe shoe) {
        this.shoes.add(shoe);
        shoe.getPeople().add(this);
        return this;
    }

    public Person removeShoe(Shoe shoe) {
        this.shoes.remove(shoe);
        shoe.getPeople().remove(this);
        return this;
    }

    public void setShoes(Set<Shoe> shoes) {
        this.shoes = shoes;
    }

    public Driver getDriver() {
        return this.driver;
    }

    public Person driver(Driver driver) {
        this.setDriver(driver);
        return this;
    }

    public void setDriver(Driver driver) {
        if (this.driver != null) {
            this.driver.setPerson(null);
        }
        if (driver != null) {
            driver.setPerson(this);
        }
        this.driver = driver;
    }

    public Set<Car> getCars() {
        return this.cars;
    }

    public Person cars(Set<Car> cars) {
        this.setCars(cars);
        return this;
    }

    public Person addCar(Car car) {
        this.cars.add(car);
        car.getPassengers().add(this);
        return this;
    }

    public Person removeCar(Car car) {
        this.cars.remove(car);
        car.getPassengers().remove(this);
        return this;
    }

    public void setCars(Set<Car> cars) {
        if (this.cars != null) {
            this.cars.forEach(i -> i.removePassengers(this));
        }
        if (cars != null) {
            cars.forEach(i -> i.addPassengers(this));
        }
        this.cars = cars;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Person)) {
            return false;
        }
        return id != null && id.equals(((Person) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Person{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            "}";
    }
}
