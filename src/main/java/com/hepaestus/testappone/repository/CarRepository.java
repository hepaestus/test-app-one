package com.hepaestus.testappone.repository;

import com.hepaestus.testappone.domain.Car;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Car entity.
 */
@Repository
public interface CarRepository extends JpaRepository<Car, Long> {
    @Query(
        value = "select distinct car from Car car left join fetch car.passengers",
        countQuery = "select count(distinct car) from Car car"
    )
    Page<Car> findAllWithEagerRelationships(Pageable pageable);

    @Query("select distinct car from Car car left join fetch car.passengers")
    List<Car> findAllWithEagerRelationships();

    @Query("select car from Car car left join fetch car.passengers where car.id =:id")
    Optional<Car> findOneWithEagerRelationships(@Param("id") Long id);
}
