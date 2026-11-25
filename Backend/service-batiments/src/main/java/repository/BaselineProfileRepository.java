package repository;

import entity.BaselineProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BaselineProfileRepository extends JpaRepository<BaselineProfile, Long> {
    List<BaselineProfile> findByBuildingIdOrderByComputedAtDesc(Long buildingId);
}