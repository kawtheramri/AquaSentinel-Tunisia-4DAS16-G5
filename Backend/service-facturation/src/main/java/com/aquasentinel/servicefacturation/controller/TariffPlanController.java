package com.aquasentinel.servicefacturation.controller;

import com.aquasentinel.servicefacturation.entity.TariffPlan;
import com.aquasentinel.servicefacturation.service.TariffPlanService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tariff-plans")
public class TariffPlanController {

    private final TariffPlanService service;

    public TariffPlanController(TariffPlanService service) {
        this.service = service;
    }

    @GetMapping
    public List<TariffPlan> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public TariffPlan getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TariffPlan create(@RequestBody TariffPlan plan) {
        return service.create(plan);
    }

    @PutMapping("/{id}")
    public TariffPlan update(@PathVariable Long id, @RequestBody TariffPlan plan) {
        return service.update(id, plan);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}