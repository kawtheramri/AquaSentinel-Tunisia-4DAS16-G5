package com.aquasentinel.serviceconsommation.repository;

import com.aquasentinel.serviceconsommation.entity.MeasurementCorrection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MeasurementCorrectionRepository extends JpaRepository<MeasurementCorrection, Long> {

    List<MeasurementCorrection> findByMeasurementIdOrderByCorrectedAtDesc(Long measurementId);
}
