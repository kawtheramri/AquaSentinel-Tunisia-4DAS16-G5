package com.aquasentinel.serviceconsommation.service;

import com.aquasentinel.serviceconsommation.entity.MeasureStatus;
import com.aquasentinel.serviceconsommation.entity.MeasurementCorrection;
import com.aquasentinel.serviceconsommation.entity.WaterMeasurement;
import com.aquasentinel.serviceconsommation.exception.ConflictException;
import com.aquasentinel.serviceconsommation.exception.NotFoundException;
import com.aquasentinel.serviceconsommation.repository.MeasurementCorrectionRepository;
import com.aquasentinel.serviceconsommation.repository.WaterMeasurementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WaterMeasurementService {

    private final WaterMeasurementRepository measurementRepo;
    private final MeasurementCorrectionRepository correctionRepo;

    // ---------- CRUD ----------
    public List<WaterMeasurement> findAll() {
        return measurementRepo.findAll();
    }

    public List<WaterMeasurement> findByBuilding(Long buildingId) {
        return measurementRepo.findByBuildingIdOrderByMeasuredAtDesc(buildingId);
    }

    public WaterMeasurement findById(Long id) {
        return measurementRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Relevé introuvable : " + id));
    }

    public WaterMeasurement create(WaterMeasurement m) {
        if (measurementRepo.existsByMeterRefAndMeasuredAt(m.getMeterRef(), m.getMeasuredAt())) {
            throw new ConflictException("Un relevé existe déjà pour le compteur " + m.getMeterRef()
                    + " à " + m.getMeasuredAt());
        }
        m.setId(null);
        m.setConsumptionM3(calculateConsumption(m));
        m.setStatus(MeasureStatus.VALID);
        m.setCreatedAt(LocalDateTime.now());
        return measurementRepo.save(m);
    }

    public WaterMeasurement update(Long id, WaterMeasurement data) {
        WaterMeasurement m = findById(id);
        boolean keyChanged = !m.getMeterRef().equals(data.getMeterRef())
                || !m.getMeasuredAt().equals(data.getMeasuredAt());
        if (keyChanged && measurementRepo.existsByMeterRefAndMeasuredAt(data.getMeterRef(), data.getMeasuredAt())) {
            throw new ConflictException("Un relevé existe déjà pour le compteur " + data.getMeterRef()
                    + " à " + data.getMeasuredAt());
        }
        m.setBuildingId(data.getBuildingId());
        m.setMeterRef(data.getMeterRef());
        m.setIndexM3(data.getIndexM3());
        m.setMeasuredAt(data.getMeasuredAt());
        m.setSource(data.getSource());
        m.setRecordedBy(data.getRecordedBy());
        m.setConsumptionM3(calculateConsumption(m));
        return measurementRepo.save(m);
    }

    public void delete(Long id) {
        WaterMeasurement m = findById(id);   // 404 si inexistant
        measurementRepo.delete(m);           // les corrections sont supprimées en cascade
    }

    // ---------- Corrections ----------
    @Transactional
    public MeasurementCorrection addCorrection(Long measurementId, MeasurementCorrection correction) {
        WaterMeasurement m = findById(measurementId);

        correction.setId(null);
        correction.setMeasurement(m);
        correction.setOldIndexM3(m.getIndexM3());
        correction.setCorrectedAt(LocalDateTime.now());

        m.setIndexM3(correction.getNewIndexM3());
        m.setConsumptionM3(calculateConsumption(m));
        m.setStatus(MeasureStatus.CORRECTED);
        m.getCorrections().add(correction);

        return correctionRepo.save(correction);
    }

    public List<MeasurementCorrection> getCorrections(Long measurementId) {
        findById(measurementId);
        return correctionRepo.findByMeasurementIdOrderByCorrectedAtDesc(measurementId);
    }

    // consommation = index actuel - index du relevé précédent du même compteur
    private BigDecimal calculateConsumption(WaterMeasurement m) {
        Optional<WaterMeasurement> previous = measurementRepo
                .findFirstByMeterRefAndMeasuredAtBeforeOrderByMeasuredAtDesc(m.getMeterRef(), m.getMeasuredAt());
        if (previous.isEmpty()) {
            return BigDecimal.ZERO;
        }
        BigDecimal consumption = m.getIndexM3().subtract(previous.get().getIndexM3());
        if (consumption.signum() < 0) {
            throw new IllegalArgumentException("L'index ne peut pas être inférieur au relevé précédent ("
                    + previous.get().getIndexM3() + " m3)");
        }
        return consumption;
    }
}
