package com.aquasentinel.servicefacturation.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "invoice_line")
@Getter
@Setter
public class InvoiceLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // N -> 1 : plusieurs lignes appartiennent à une facture
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "invoice_id", nullable = false)
    @JsonIgnore
    private WaterInvoice invoice;

    @Column(name = "tier_order", nullable = false)
    private Integer tierOrder;

    @Column(name = "quantity_m3", nullable = false)
    private Double quantityM3;

    @Column(name = "unit_price", nullable = false, precision = 12, scale = 3)
    private BigDecimal unitPrice;

    @Column(nullable = false, precision = 14, scale = 3)
    private BigDecimal amount;

    private String label;
}