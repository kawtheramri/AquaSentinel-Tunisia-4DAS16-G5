package com.aquasentinel.servicefacturation.entity;

import com.aquasentinel.servicefacturation.enums.TariffCategory;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tariff_plan")
@Getter
@Setter
public class TariffPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TariffCategory category;

    @Column(name = "fixed_fee", nullable = false, precision = 12, scale = 3)
    private BigDecimal fixedFee = BigDecimal.ZERO;

    @Column(name = "surcharge_rate", nullable = false, precision = 8, scale = 4)
    private BigDecimal surchargeRate = BigDecimal.ZERO;

    @Column(name = "tax_rate", nullable = false, precision = 8, scale = 4)
    private BigDecimal taxRate = BigDecimal.ZERO;

    @Column(name = "valid_from", nullable = false)
    private LocalDate validFrom;

    @Column(nullable = false)
    private Boolean active = true;

    // 1 -> N : un plan possède plusieurs tranches
    @OneToMany(mappedBy = "tariffPlan", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("tierOrder ASC")
    private List<TariffTier> tiers = new ArrayList<>();
}