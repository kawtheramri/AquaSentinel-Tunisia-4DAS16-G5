package entity;

import entity.enums.MaintenanceStatus;
import entity.enums.Priority;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "maintenance_requests")
@Getter
@Setter
@NoArgsConstructor
public class MaintenanceRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long buildingId;

    private String leakDetectionId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MaintenanceStatus status = MaintenanceStatus.OPEN;

    private LocalDate scheduledDate;

    private LocalDateTime completedAt;

    private Double estimatedLossM3Day;

    @Column(precision = 12, scale = 2)
    private BigDecimal laborCost = BigDecimal.ZERO;

    @Column(precision = 12, scale = 2)
    private BigDecimal partsCost = BigDecimal.ZERO;

    // ---------- Compositions ----------

    @OneToMany(mappedBy = "maintenanceRequest",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<TechnicianAssignment> assignments = new ArrayList<>();

    @OneToMany(mappedBy = "maintenanceRequest",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<PartUsage> parts = new ArrayList<>();

    @OneToOne(mappedBy = "maintenanceRequest",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private InterventionReport report;

    @OneToMany(mappedBy = "maintenanceRequest",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @OrderBy("changedAt ASC")
    private List<StatusHistory> history = new ArrayList<>();
}
