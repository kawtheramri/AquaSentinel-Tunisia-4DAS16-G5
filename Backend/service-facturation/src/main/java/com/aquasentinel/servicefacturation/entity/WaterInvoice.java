package com.aquasentinel.servicefacturation.entity;

import com.aquasentinel.servicefacturation.enums.InvoiceStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "water_invoice")
@Getter
@Setter
public class WaterInvoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "invoice_number", nullable = false, unique = true, length = 50)
    private String invoiceNumber;

    // Simple Long, sans clé étrangère : le bâtiment est dans un autre microservice
    @Column(name = "building_id", nullable = false)
    private Long buildingId;

    // N -> 1 : plusieurs factures utilisent un même plan tarifaire
    @ManyToOne(optional = false)
    @JoinColumn(name = "tariff_plan_id", nullable = false)
    private TariffPlan tariffPlan;

    @Column(name = "period_start", nullable = false)
    private LocalDate periodStart;

    @Column(name = "period_end", nullable = false)
    private LocalDate periodEnd;

    @Column(name = "consumption_m3", nullable = false)
    private Double consumptionM3 = 0.0;

    @Column(name = "quota_m3", nullable = false)
    private Double quotaM3 = 0.0;

    @Column(name = "base_amount", nullable = false, precision = 14, scale = 3)
    private BigDecimal baseAmount = BigDecimal.ZERO;

    @Column(name = "surcharge_amount", nullable = false, precision = 14, scale = 3)
    private BigDecimal surchargeAmount = BigDecimal.ZERO;

    @Column(name = "fixed_fees", nullable = false, precision = 14, scale = 3)
    private BigDecimal fixedFees = BigDecimal.ZERO;

    @Column(name = "tax_amount", nullable = false, precision = 14, scale = 3)
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @Column(name = "total_amount", nullable = false, precision = 14, scale = 3)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvoiceStatus status = InvoiceStatus.DRAFT;

    // 1 -> N : une facture possède plusieurs lignes
    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("tierOrder ASC")
    private List<InvoiceLine> lines = new ArrayList<>();

    // 1 -> N : une facture possède plusieurs estimations d'économies
    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SavingsEstimate> savingsEstimates = new ArrayList<>();
}