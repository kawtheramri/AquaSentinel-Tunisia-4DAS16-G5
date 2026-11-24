package com.aquasentinel.servicedetectionfuites.controller;

import com.aquasentinel.servicedetectionfuites.model.LeakDetection;
import com.aquasentinel.servicedetectionfuites.model.LeakStatusHistory;
import com.aquasentinel.servicedetectionfuites.model.LeakVerification;
import com.aquasentinel.servicedetectionfuites.service.LeakDetectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/leaks")
@RequiredArgsConstructor
public class LeakDetectionController {

    private final LeakDetectionService service;

    @GetMapping
    public List<LeakDetection> getAll(@RequestParam(required = false) Long buildingId) {
        return buildingId == null ? service.findAll() : service.findByBuilding(buildingId);
    }

    @GetMapping("/{id}")
    public LeakDetection getById(@PathVariable String id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LeakDetection create(@RequestBody LeakDetection leak) {
        return service.create(leak);
    }

    @PutMapping("/{id}")
    public LeakDetection update(@PathVariable String id, @RequestBody LeakDetection leak) {
        return service.update(id, leak);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        service.delete(id);
    }

    // ----- actions métier -----
    @PatchMapping("/{id}/verify")
    public LeakDetection verify(@PathVariable String id, @RequestBody Map<String, String> body) {
        Long verifiedById = body.get("verifiedBy") != null ? Long.valueOf(body.get("verifiedBy")) : null;
        return service.verify(id, verifiedById, body.get("comment"));
    }

    @PatchMapping("/{id}/false-positive")
    public LeakDetection falsePositive(@PathVariable String id, @RequestBody Map<String, String> body) {
        Long verifiedById = body.get("verifiedBy") != null ? Long.valueOf(body.get("verifiedBy")) : null;
        return service.markFalsePositive(id, verifiedById, body.get("comment"));
    }

    @GetMapping("/{id}/verifications")
    public List<LeakVerification> verifications(@PathVariable String id) {
        return service.verifications(id);
    }

    @GetMapping("/{id}/history")
    public List<LeakStatusHistory> history(@PathVariable String id) {
        return service.history(id);
    }
}
