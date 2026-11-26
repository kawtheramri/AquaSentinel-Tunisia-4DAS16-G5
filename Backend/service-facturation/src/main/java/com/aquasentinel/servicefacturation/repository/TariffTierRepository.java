package com.aquasentinel.servicefacturation.repository;

import com.aquasentinel.servicefacturation.entity.TariffTier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TariffTierRepository extends JpaRepository<TariffTier, Long> {
}