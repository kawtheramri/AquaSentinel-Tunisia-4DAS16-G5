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
@Table(name = "technician_assignments")
@Getter
@Setter
@NoArgsConstructor
public class TechnicianAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long technicianId;          // réf User.id (autre microservice)

    private LocalDateTime assignedAt;

    private LocalDateTime unassignedAt;

    private String reason;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "maintenance_request_id")
    private MaintenanceRequest maintenanceRequest;
}