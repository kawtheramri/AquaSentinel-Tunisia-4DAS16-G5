package com.aquasentinel.serviceconsommation.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "water_measurement",
        uniqueConstraints = @UniqueConstraint(columnNames = {"meter_ref", "measured_at"}),
        indexes = @Index(columnList = "building_id, measured_at"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WaterMeasurement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "building_id", nullable = false)
    private Long buildingId;              // réf. Building.id

    @NotBlank
    @Column(name = "meter_ref", nullable = false, length = 50)
    private String meterRef;              // réf. Meter.meterRef

    @NotNull
    @PositiveOrZero
    @Column(name = "index_m3", nullable = false, precision = 12, scale = 3)
    private BigDecimal indexM3;

    @Column(name = "consumption_m3", precision = 12, scale = 3)
    private BigDecimal consumptionM3;     // calculée : index - index précédent

    @NotNull
    @Column(name = "measured_at", nullable = false)
    private LocalDateTime measuredAt;

    @NotNull
    @Enumerated(EnumType.STRING)
    private MeasureSource source;

    @Enumerated(EnumType.STRING)
    private MeasureStatus status;

    @Column(name = "recorded_by")
    private Long recordedBy;              // réf. JWT (utilisateur)

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // composition : un relevé possède ses corrections
    @JsonIgnore
    @Builder.Default
    @OneToMany(mappedBy = "measurement", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MeasurementCorrection> corrections = new ArrayList<>();
}
