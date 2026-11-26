package com.aquasentinel.servicepiecesrechange.repository;

import com.aquasentinel.servicepiecesrechange.model.PartCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PartCategoryRepository extends JpaRepository<PartCategory, Long> {
    Optional<PartCategory> findByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCase(String name);
}
