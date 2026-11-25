package entity;

import entity.MaintenanceRequest;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import entity.enums.MaintenanceStatus;
import entity.enums.Priority;

import java.time.LocalDateTime;

@Entity
@Table(name = "status_history")
@Getter
@Setter
@NoArgsConstructor
public class StatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private MaintenanceStatus oldStatus;

    @Enumerated(EnumType.STRING)
    private MaintenanceStatus newStatus;

    private LocalDateTime changedAt;

    private Long changedBy;             // réf JWT (id de l'utilisateur)

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "maintenance_request_id")
    private MaintenanceRequest maintenanceRequest;
}