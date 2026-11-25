package com.aquasentinel.serviceconsommation.service;

import com.aquasentinel.serviceconsommation.entity.MeasureSource;
import com.aquasentinel.serviceconsommation.entity.MeasureStatus;
import com.aquasentinel.serviceconsommation.entity.MeasurementCorrection;
import com.aquasentinel.serviceconsommation.entity.WaterMeasurement;
import com.aquasentinel.serviceconsommation.exception.ConflictException;
import com.aquasentinel.serviceconsommation.repository.MeasurementCorrectionRepository;
import com.aquasentinel.serviceconsommation.repository.WaterMeasurementRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WaterMeasurementServiceTest {

    @Mock
    private WaterMeasurementRepository measurementRepo;

    @Mock
    private MeasurementCorrectionRepository correctionRepo;

    @InjectMocks
    private WaterMeasurementService service;

    private WaterMeasurement newMeasurement(String index, LocalDateTime date) {
        return WaterMeasurement.builder()
                .buildingId(1L)
                .meterRef("CPT-001")
                .indexM3(new BigDecimal(index))
                .measuredAt(date)
                .source(MeasureSource.METER)
                .build();
    }

    @Test
    void create_calculeLaConsommationAvecLeRelevePrecedent() {
        LocalDateTime date = LocalDateTime.of(2026, 11, 2, 8, 0);
        WaterMeasurement previous = newMeasurement("100.000", date.minusDays(1));
        when(measurementRepo.findFirstByMeterRefAndMeasuredAtBeforeOrderByMeasuredAtDesc("CPT-001", date))
                .thenReturn(Optional.of(previous));
        when(measurementRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        WaterMeasurement saved = service.create(newMeasurement("112.500", date));

        assertEquals(0, new BigDecimal("12.500").compareTo(saved.getConsumptionM3()));
        assertEquals(MeasureStatus.VALID, saved.getStatus());
        assertNotNull(saved.getCreatedAt());
    }

    @Test
    void create_premierReleve_consommationZero() {
        LocalDateTime date = LocalDateTime.of(2026, 11, 1, 8, 0);
        when(measurementRepo.findFirstByMeterRefAndMeasuredAtBeforeOrderByMeasuredAtDesc("CPT-001", date))
                .thenReturn(Optional.empty());
        when(measurementRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        WaterMeasurement saved = service.create(newMeasurement("100.000", date));

        assertEquals(0, BigDecimal.ZERO.compareTo(saved.getConsumptionM3()));
    }

    @Test
    void create_releveEnDouble_conflit() {
        LocalDateTime date = LocalDateTime.of(2026, 11, 1, 8, 0);
        when(measurementRepo.existsByMeterRefAndMeasuredAt("CPT-001", date)).thenReturn(true);

        assertThrows(ConflictException.class, () -> service.create(newMeasurement("100.000", date)));
        verify(measurementRepo, never()).save(any());
    }

    @Test
    void addCorrection_changeIndexEtStatutCorrected() {
        LocalDateTime date = LocalDateTime.of(2026, 11, 2, 8, 0);
        WaterMeasurement measurement = newMeasurement("150.000", date);
        measurement.setId(5L);
        measurement.setStatus(MeasureStatus.VALID);
        when(measurementRepo.findById(5L)).thenReturn(Optional.of(measurement));
        when(measurementRepo.findFirstByMeterRefAndMeasuredAtBeforeOrderByMeasuredAtDesc("CPT-001", date))
                .thenReturn(Optional.of(newMeasurement("100.000", date.minusDays(1))));
        when(correctionRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        MeasurementCorrection correction = new MeasurementCorrection();
        correction.setNewIndexM3(new BigDecimal("110.000"));
        correction.setReason("Erreur de saisie");

        MeasurementCorrection saved = service.addCorrection(5L, correction);

        assertEquals(0, new BigDecimal("150.000").compareTo(saved.getOldIndexM3()));
        assertEquals(MeasureStatus.CORRECTED, measurement.getStatus());
        assertEquals(0, new BigDecimal("110.000").compareTo(measurement.getIndexM3()));
        assertEquals(0, new BigDecimal("10.000").compareTo(measurement.getConsumptionM3()));
    }
}
