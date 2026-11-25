package com.aquasentinel.serviceconsommation.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "consumption_summary",
        uniqueConstraints = @UniqueConstraint(columnNames = {"building_id", "period_type", "period_start"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsumptionSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "building_id", nullable = false)
    private Long buildingId;              // réf. Building.id

    @Enumerated(EnumType.STRING)
    @Column(name = "period_type", nullable = false)
    private PeriodType periodType;

    @Column(name = "period_start", nullable = false)
    private LocalDate periodStart;

    @Column(name = "period_end", nullable = false)
    private LocalDate periodEnd;

    @Column(name = "total_m3", precision = 14, scale = 3)
    private BigDecimal totalM3;

    @Column(name = "measurement_count")
    private Integer measurementCount;

    @Column(name = "variation_pct", precision = 6, scale = 2)
    private BigDecimal variationPct;      // par rapport à la période précédente

    @Column(name = "computed_at")
    private LocalDateTime computedAt;
}
