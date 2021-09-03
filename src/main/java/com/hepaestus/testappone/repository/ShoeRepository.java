package com.hepaestus.testappone.repository;

import com.hepaestus.testappone.domain.Shoe;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Shoe entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ShoeRepository extends JpaRepository<Shoe, Long> {}
