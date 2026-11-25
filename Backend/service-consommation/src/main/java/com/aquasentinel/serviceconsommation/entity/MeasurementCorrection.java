package com.aquasentinel.serviceconsommation.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "measurement_correction",
        indexes = @Index(columnList = "measurement_id"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeasurementCorrection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "measurement_id")
    private WaterMeasurement measurement;

    @Column(name = "old_index_m3", precision = 12, scale = 3)
    private BigDecimal oldIndexM3;

    @NotNull
    @PositiveOrZero
    @Column(name = "new_index_m3", nullable = false, precision = 12, scale = 3)
    private BigDecimal newIndexM3;

    @NotBlank
    @Column(nullable = false)
    private String reason;                // obligatoire

    @Column(name = "corrected_by")
    private Long correctedBy;             // réf. JWT (utilisateur)

    @Column(name = "corrected_at")
    private LocalDateTime correctedAt;
}
