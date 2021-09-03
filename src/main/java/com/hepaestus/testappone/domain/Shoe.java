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
 * A Shoe.
 */
@Entity
@Table(name = "shoe")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "shoe")
public class Shoe implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Min(value = 4)
    @Max(value = 18)
    @Column(name = "shoe_size")
    private Integer shoeSize;

    @ManyToMany(mappedBy = "shoes")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "user", "shoes", "driver", "cars" }, allowSetters = true)
    private Set<Person> people = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Shoe id(Long id) {
        this.id = id;
        return this;
    }

    public Integer getShoeSize() {
        return this.shoeSize;
    }

    public Shoe shoeSize(Integer shoeSize) {
        this.shoeSize = shoeSize;
        return this;
    }

    public void setShoeSize(Integer shoeSize) {
        this.shoeSize = shoeSize;
    }

    public Set<Person> getPeople() {
        return this.people;
    }

    public Shoe people(Set<Person> people) {
        this.setPeople(people);
        return this;
    }

    public Shoe addPerson(Person person) {
        this.people.add(person);
        person.getShoes().add(this);
        return this;
    }

    public Shoe removePerson(Person person) {
        this.people.remove(person);
        person.getShoes().remove(this);
        return this;
    }

    public void setPeople(Set<Person> people) {
        if (this.people != null) {
            this.people.forEach(i -> i.removeShoe(this));
        }
        if (people != null) {
            people.forEach(i -> i.addShoe(this));
        }
        this.people = people;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Shoe)) {
            return false;
        }
        return id != null && id.equals(((Shoe) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Shoe{" +
            "id=" + getId() +
            ", shoeSize=" + getShoeSize() +
            "}";
    }
}
