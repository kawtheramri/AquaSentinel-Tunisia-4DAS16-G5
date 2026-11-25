package entity;

import entity.MaintenanceRequest;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "part_usages")
@Getter
@Setter
@NoArgsConstructor
public class PartUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String partReference;       // réf SparePart.reference (autre microservice)

    private Integer quantity;

    @Column(precision = 12, scale = 2)
    private BigDecimal unitPrice;

    private String reservationStatus;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "maintenance_request_id")
    private MaintenanceRequest maintenanceRequest;
}