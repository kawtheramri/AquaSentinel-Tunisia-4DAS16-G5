package com.aquasentinel.servicefacturation.repository;

import com.aquasentinel.servicefacturation.entity.SavingsEstimate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SavingsEstimateRepository extends JpaRepository<SavingsEstimate, Long> {
}