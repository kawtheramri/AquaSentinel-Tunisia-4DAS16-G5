package com.aquasentinel.servicefacturation.repository;

import com.aquasentinel.servicefacturation.entity.TariffPlan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TariffPlanRepository extends JpaRepository<TariffPlan, Long> {
}