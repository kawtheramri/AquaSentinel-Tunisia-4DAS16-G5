package repository;

import entity.Meter;
import entity.enums.MeterStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MeterRepository extends JpaRepository<Meter, Long> {
    Optional<Meter> findByMeterRef(String meterRef);
    boolean existsByMeterRef(String meterRef);
    List<Meter> findByBuildingId(Long buildingId);
    List<Meter> findByBuildingIdAndStatus(Long buildingId, MeterStatus status);
}