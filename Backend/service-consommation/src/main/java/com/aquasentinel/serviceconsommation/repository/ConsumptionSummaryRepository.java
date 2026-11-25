package com.aquasentinel.serviceconsommation.repository;

import com.aquasentinel.serviceconsommation.entity.ConsumptionSummary;
import com.aquasentinel.serviceconsommation.entity.PeriodType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ConsumptionSummaryRepository extends JpaRepository<ConsumptionSummary, Long> {

    List<ConsumptionSummary> findByBuildingIdOrderByPeriodStartDesc(Long buildingId);

    Optional<ConsumptionSummary> findByBuildingIdAndPeriodTypeAndPeriodStart(Long buildingId, PeriodType periodType, LocalDate periodStart);
}
