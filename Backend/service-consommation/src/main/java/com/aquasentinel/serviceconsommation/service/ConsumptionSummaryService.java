package com.aquasentinel.serviceconsommation.service;

import com.aquasentinel.serviceconsommation.entity.ConsumptionSummary;
import com.aquasentinel.serviceconsommation.entity.PeriodType;
import com.aquasentinel.serviceconsommation.entity.WaterMeasurement;
import com.aquasentinel.serviceconsommation.exception.NotFoundException;
import com.aquasentinel.serviceconsommation.repository.ConsumptionSummaryRepository;
import com.aquasentinel.serviceconsommation.repository.WaterMeasurementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConsumptionSummaryService {

    private final ConsumptionSummaryRepository summaryRepo;
    private final WaterMeasurementRepository measurementRepo;

    public List<ConsumptionSummary> findAll() {
        return summaryRepo.findAll();
    }

    public List<ConsumptionSummary> findByBuilding(Long buildingId) {
        return summaryRepo.findByBuildingIdOrderByPeriodStartDesc(buildingId);
    }

    public ConsumptionSummary findById(Long id) {
        return summaryRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Résumé introuvable : " + id));
    }

    public void delete(Long id) {
        findById(id);
        summaryRepo.deleteById(id);
    }

    // calcule (ou recalcule) le total d'un bâtiment pour une période
    public ConsumptionSummary compute(Long buildingId, PeriodType periodType, LocalDate periodStart) {
        if (buildingId == null || periodType == null || periodStart == null) {
            throw new IllegalArgumentException("buildingId, periodType et periodStart sont obligatoires");
        }
        LocalDate periodEnd = switch (periodType) {
            case DAY -> periodStart;
            case WEEK -> periodStart.plusDays(6);
            case MONTH -> periodStart.plusMonths(1).minusDays(1);
        };

        List<WaterMeasurement> measurements = measurementRepo.findByBuildingIdAndMeasuredAtBetween(
                buildingId, periodStart.atStartOfDay(), periodEnd.atTime(LocalTime.MAX));

        BigDecimal total = BigDecimal.ZERO;
        for (WaterMeasurement m : measurements) {
            if (m.getConsumptionM3() != null) {
                total = total.add(m.getConsumptionM3());
            }
        }

        // un seul résumé par (bâtiment, type, début) : on met à jour s'il existe déjà
        ConsumptionSummary summary = summaryRepo
                .findByBuildingIdAndPeriodTypeAndPeriodStart(buildingId, periodType, periodStart)
                .orElse(new ConsumptionSummary());
        summary.setBuildingId(buildingId);
        summary.setPeriodType(periodType);
        summary.setPeriodStart(periodStart);
        summary.setPeriodEnd(periodEnd);
        summary.setTotalM3(total);
        summary.setMeasurementCount(measurements.size());
        summary.setVariationPct(variation(buildingId, periodType, periodStart, total));
        summary.setComputedAt(LocalDateTime.now());
        return summaryRepo.save(summary);
    }

    // variation en % par rapport au résumé de la période précédente (null s'il n'existe pas)
    private BigDecimal variation(Long buildingId, PeriodType periodType, LocalDate periodStart, BigDecimal total) {
        LocalDate previousStart = switch (periodType) {
            case DAY -> periodStart.minusDays(1);
            case WEEK -> periodStart.minusWeeks(1);
            case MONTH -> periodStart.minusMonths(1);
        };
        return summaryRepo.findByBuildingIdAndPeriodTypeAndPeriodStart(buildingId, periodType, previousStart)
                .filter(previous -> previous.getTotalM3() != null && previous.getTotalM3().signum() > 0)
                .map(previous -> total.subtract(previous.getTotalM3())
                        .multiply(BigDecimal.valueOf(100))
                        .divide(previous.getTotalM3(), 2, RoundingMode.HALF_UP))
                .orElse(null);
    }
}
