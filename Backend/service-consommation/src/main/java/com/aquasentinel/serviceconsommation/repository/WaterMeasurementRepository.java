package com.aquasentinel.serviceconsommation.repository;

import com.aquasentinel.serviceconsommation.entity.WaterMeasurement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface WaterMeasurementRepository extends JpaRepository<WaterMeasurement, Long> {

    List<WaterMeasurement> findByBuildingIdOrderByMeasuredAtDesc(Long buildingId);

    boolean existsByMeterRefAndMeasuredAt(String meterRef, LocalDateTime measuredAt);

    // dernier relevé du même compteur avant cette date (pour calculer la consommation)
    Optional<WaterMeasurement> findFirstByMeterRefAndMeasuredAtBeforeOrderByMeasuredAtDesc(String meterRef, LocalDateTime measuredAt);

    List<WaterMeasurement> findByBuildingIdAndMeasuredAtBetween(Long buildingId, LocalDateTime start, LocalDateTime end);
}
