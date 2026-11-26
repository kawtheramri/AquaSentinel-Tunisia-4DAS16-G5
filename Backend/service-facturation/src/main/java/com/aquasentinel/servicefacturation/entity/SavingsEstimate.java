package com.aquasentinel.servicefacturation.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "savings_estimate")
@Getter
@Setter
public class SavingsEstimate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // N -> 1 : plusieurs estimations appartiennent à une facture
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "invoice_id", nullable = false)
    @JsonIgnore
    private WaterInvoice invoice;

    // Références logiques vers d'autres microservices (pas de clé étrangère)
    @Column(name = "maintenance_request_id")
    private Long maintenanceRequestId;

    @Column(name = "leak_detection_id", length = 100)
    private String leakDetectionId;

    @Column(name = "saved_m3", nullable = false)
    private Double savedM3 = 0.0;

    @Column(name = "saved_amount", nullable = false, precision = 14, scale = 3)
    private BigDecimal savedAmount = BigDecimal.ZERO;

    @Column(name = "estimated_at", nullable = false)
    private LocalDateTime estimatedAt = LocalDateTime.now();
}