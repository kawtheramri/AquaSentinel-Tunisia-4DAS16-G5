package com.aquasentinel.servicedetectionfuites.controller;

import com.aquasentinel.servicedetectionfuites.exception.NotFoundException;
import com.aquasentinel.servicedetectionfuites.model.DetectionRule;
import com.aquasentinel.servicedetectionfuites.repository.DetectionRuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rules")
@RequiredArgsConstructor
public class DetectionRuleController {

    private final DetectionRuleRepository repo;

    @GetMapping
    public List<DetectionRule> getAll() { return repo.findAll(); }

    @GetMapping("/{id}")
    public DetectionRule getById(@PathVariable String id) {
        return repo.findById(id).orElseThrow(() -> new NotFoundException("Règle introuvable : " + id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DetectionRule create(@RequestBody DetectionRule rule) {
        rule.setId(null);
        return repo.save(rule);
    }

    @PutMapping("/{id}")
    public DetectionRule update(@PathVariable String id, @RequestBody DetectionRule rule) {
        getById(id);
        rule.setId(id);
        return repo.save(rule);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        getById(id);
        repo.deleteById(id);
    }
}
