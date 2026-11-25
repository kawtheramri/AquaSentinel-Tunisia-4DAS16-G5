package repository;

import entity.ManagerAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ManagerAssignmentRepository extends JpaRepository<ManagerAssignment, Long> {
    List<ManagerAssignment> findByBuildingIdOrderByAssignedAtDesc(Long buildingId);
    Optional<ManagerAssignment> findFirstByBuildingIdAndUnassignedAtIsNullOrderByAssignedAtDesc(Long buildingId);
}