package com.aquasentinel.serviceconsommation.controller;

import com.aquasentinel.serviceconsommation.entity.ConsumptionSummary;
import com.aquasentinel.serviceconsommation.entity.PeriodType;
import com.aquasentinel.serviceconsommation.service.ConsumptionSummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/summaries")
@RequiredArgsConstructor
public class ConsumptionSummaryController {

    private final ConsumptionSummaryService service;

    @GetMapping
    public List<ConsumptionSummary> getAll(@RequestParam(required = false) Long buildingId) {
        return buildingId == null ? service.findAll() : service.findByBuilding(buildingId);
    }

    @GetMapping("/{id}")
    public ConsumptionSummary getById(@PathVariable Long id) {
        return service.findById(id);
    }

    // ex : POST /api/summaries/compute?buildingId=1&periodType=MONTH&periodStart=2026-11-01
    @PostMapping("/compute")
    public ConsumptionSummary compute(@RequestParam Long buildingId,
                                      @RequestParam PeriodType periodType,
                                      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodStart) {
        return service.compute(buildingId, periodType, periodStart);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
