package entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import entity.enums.MaintenanceStatus;
import entity.enums.Priority;


import java.time.LocalDateTime;

@Entity
@Table(name = "intervention_reports")
@Getter
@Setter
@NoArgsConstructor
public class InterventionReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 2000)
    private String summary;

    @Column(length = 2000)
    private String workDone;

    private Double durationHours;

    private LocalDateTime writtenAt;

    private Long writtenBy;             // réf JWT (id de l'utilisateur)

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "maintenance_request_id", unique = true)
    private entity.MaintenanceRequest maintenanceRequest;
}