package com.aquasentinel.servicefacturation.service;

import com.aquasentinel.servicefacturation.entity.TariffPlan;
import com.aquasentinel.servicefacturation.entity.TariffTier;
import com.aquasentinel.servicefacturation.exception.ResourceNotFoundException;
import com.aquasentinel.servicefacturation.repository.TariffPlanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TariffPlanService {

    private final TariffPlanRepository repository;

    public TariffPlanService(TariffPlanRepository repository) {
        this.repository = repository;
    }

    public List<TariffPlan> findAll() {
        return repository.findAll();
    }

    public TariffPlan findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plan tarifaire introuvable : " + id));
    }

    @Transactional
    public TariffPlan create(TariffPlan plan) {
        plan.setId(null);
        if (plan.getTiers() != null) {
            for (TariffTier tier : plan.getTiers()) {
                tier.setId(null);
                tier.setTariffPlan(plan);
            }
        }
        return repository.save(plan);
    }

    @Transactional
    public TariffPlan update(Long id, TariffPlan data) {
        TariffPlan plan = findById(id);
        plan.setCategory(data.getCategory());
        plan.setFixedFee(data.getFixedFee());
        plan.setSurchargeRate(data.getSurchargeRate());
        plan.setTaxRate(data.getTaxRate());
        plan.setValidFrom(data.getValidFrom());
        plan.setActive(data.getActive());
        return repository.save(plan);
    }

    @Transactional
    public void delete(Long id) {
        TariffPlan plan = findById(id);
        repository.delete(plan);
    }
}