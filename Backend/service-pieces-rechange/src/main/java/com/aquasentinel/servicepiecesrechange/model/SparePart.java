package com.aquasentinel.servicepiecesrechange.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Entité principale du microservice Spare Parts avec associations JPA (PartCategory, Supplier, Reservation, StockMovement). */
@Entity
@Table(name = "spare_parts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SparePart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "La référence est obligatoire")
    @Column(nullable = false, unique = true, length = 64)
    private String reference;

    @NotBlank(message = "Le nom de la pièce est obligatoire")
    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(length = 30)
    private String unit;

    @NotNull(message = "La quantité en stock est obligatoire")
    @Min(value = 0, message = "La quantité en stock ne peut pas être négative")
    @Column(nullable = false)
    private Integer quantityInStock;

    @Min(value = 0, message = "La quantité réservée ne peut pas être négative")
    @Column(nullable = false)
    private Integer reservedQuantity;

    @NotNull(message = "Le seuil minimal est obligatoire")
    @Min(value = 0, message = "Le seuil minimal ne peut pas être négatif")
    @Column(nullable = false)
    private Integer minThreshold;

    @Min(value = 1, message = "La capacité maximale doit être au moins 1")
    private Integer maxCapacity;

    @NotNull(message = "Le prix unitaire est obligatoire")
    @DecimalMin(value = "0.0", message = "Le prix unitaire ne peut pas être négatif")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PartStatus status;

    @Column(length = 120)
    private String storageLocation;

    private LocalDateTime lastRestockedAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private PartCategory category;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    @JsonIgnore
    @OneToMany(mappedBy = "sparePart", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Reservation> reservations = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "sparePart", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<StockMovement> movements = new ArrayList<>();

    @PrePersist
    public void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.reservedQuantity == null) {
            this.reservedQuantity = 0;
        }
        if (this.quantityInStock == null) {
            this.quantityInStock = 0;
        }
        if (this.minThreshold == null) {
            this.minThreshold = 0;
        }
        if (this.quantityInStock > 0 && this.lastRestockedAt == null) {
            this.lastRestockedAt = this.createdAt;
        }
        refreshStatus();
    }

    @PreUpdate
    public void onUpdate() {
        refreshStatus();
    }

    /** Stock réellement disponible pour de nouvelles réservations. */
    public int getAvailableQuantity() {
        int stock = quantityInStock != null ? quantityInStock : 0;
        int reserved = reservedQuantity != null ? reservedQuantity : 0;
        return Math.max(0, stock - reserved);
    }

    /** Recalcule automatiquement le statut (AVAILABLE, LOW_STOCK, OUT_OF_STOCK). */
    public void refreshStatus() {
        int available = getAvailableQuantity();
        int threshold = minThreshold != null ? minThreshold : 0;
        if (available <= 0) {
            this.status = PartStatus.OUT_OF_STOCK;
        } else if (available <= threshold) {
            this.status = PartStatus.LOW_STOCK;
        } else {
            this.status = PartStatus.AVAILABLE;
        }
    }

    /** Réserve une quantité pour une intervention (lève IllegalStateException si rupture). */
    public void reserve(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("La quantité à réserver doit être positive");
        }
        if (getAvailableQuantity() < quantity) {
            throw new IllegalStateException("Stock insuffisant pour " + reference);
        }
        this.reservedQuantity += quantity;
        refreshStatus();
    }

    /** Libère une quantité réservée suite à une annulation. */
    public void release(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("La quantité à libérer doit être positive");
        }
        if (this.reservedQuantity < quantity) {
            throw new IllegalStateException("Quantité réservée insuffisante pour libération");
        }
        this.reservedQuantity -= quantity;
        refreshStatus();
    }

    /** Confirme la sortie définitive du stock à la clôture d'une intervention. */
    public void confirmUsage(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("La quantité consommée doit être positive");
        }
        if (this.quantityInStock < quantity) {
            throw new IllegalStateException("Stock physique insuffisant pour sortie : " + reference);
        }
        this.quantityInStock -= quantity;
        this.reservedQuantity = Math.max(0, this.reservedQuantity - quantity);
        refreshStatus();
    }

    /** Valeur financière du stock courant. */
    public BigDecimal getStockValue() {
        if (unitPrice == null || quantityInStock == null) {
            return BigDecimal.ZERO;
        }
        return unitPrice.multiply(BigDecimal.valueOf(quantityInStock));
    }
}
