package com.aquasentinel.serviceconsommation.controller;

import com.aquasentinel.serviceconsommation.entity.MeasurementCorrection;
import com.aquasentinel.serviceconsommation.entity.WaterMeasurement;
import com.aquasentinel.serviceconsommation.service.WaterMeasurementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/measurements")
@RequiredArgsConstructor
public class WaterMeasurementController {

    private final WaterMeasurementService service;

    @GetMapping
    public List<WaterMeasurement> getAll(@RequestParam(required = false) Long buildingId) {
        return buildingId == null ? service.findAll() : service.findByBuilding(buildingId);
    }

    @GetMapping("/{id}")
    public WaterMeasurement getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WaterMeasurement create(@Valid @RequestBody WaterMeasurement measurement) {
        return service.create(measurement);
    }

    @PutMapping("/{id}")
    public WaterMeasurement update(@PathVariable Long id, @Valid @RequestBody WaterMeasurement measurement) {
        return service.update(id, measurement);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    // ----- corrections d'un relevé -----
    @PostMapping("/{id}/corrections")
    @ResponseStatus(HttpStatus.CREATED)
    public MeasurementCorrection addCorrection(@PathVariable Long id, @Valid @RequestBody MeasurementCorrection correction) {
        return service.addCorrection(id, correction);
    }

    @GetMapping("/{id}/corrections")
    public List<MeasurementCorrection> getCorrections(@PathVariable Long id) {
        return service.getCorrections(id);
    }
}
