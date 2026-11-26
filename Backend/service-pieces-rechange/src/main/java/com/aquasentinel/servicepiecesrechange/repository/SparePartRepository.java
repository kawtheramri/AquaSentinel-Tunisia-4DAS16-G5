package com.aquasentinel.servicepiecesrechange.repository;

import com.aquasentinel.servicepiecesrechange.model.PartStatus;
import com.aquasentinel.servicepiecesrechange.model.SparePart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SparePartRepository extends JpaRepository<SparePart, Long> {

    Optional<SparePart> findByReferenceIgnoreCase(String reference);

    boolean existsByReferenceIgnoreCase(String reference);

    boolean existsByCategoryId(Long categoryId);

    boolean existsBySupplierId(Long supplierId);

    @Query("""
            SELECT p FROM SparePart p
            LEFT JOIN p.category c
            LEFT JOIN p.supplier s
            WHERE (:categoryId IS NULL OR c.id = :categoryId)
              AND (:supplierId IS NULL OR s.id = :supplierId)
              AND (:status IS NULL OR p.status = :status)
              AND (:search IS NULL OR LOWER(p.reference) LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')))
            ORDER BY p.id ASC
            """)
    List<SparePart> search(
            @Param("categoryId") Long categoryId,
            @Param("supplierId") Long supplierId,
            @Param("status") PartStatus status,
            @Param("search") String search
    );
}
