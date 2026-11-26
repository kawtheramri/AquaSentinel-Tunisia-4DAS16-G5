package com.aquasentinel.servicefacturation.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "tariff_tier",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_tier_order",
                columnNames = {"tariff_plan_id", "tier_order"}))
@Getter
@Setter
public class TariffTier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // N -> 1 : plusieurs tranches appartiennent à un plan
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tariff_plan_id", nullable = false)
    @JsonIgnore
    private TariffPlan tariffPlan;

    @Column(name = "from_m3", nullable = false)
    private Double fromM3;

    @Column(name = "to_m3")
    private Double toM3;

    @Column(name = "price_per_m3", nullable = false, precision = 12, scale = 3)
    private BigDecimal pricePerM3;

    @Column(name = "tier_order", nullable = false)
    private Integer tierOrder;
}