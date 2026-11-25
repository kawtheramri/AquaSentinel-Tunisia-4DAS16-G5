package repository;

import entity.Building;
import entity.enums.BuildingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BuildingRepository extends JpaRepository<Building, Long> {
    Optional<Building> findByCode(String code);
    boolean existsByCode(String code);
    List<Building> findByStatus(BuildingStatus status);
}