package entity;

import entity.enums.MeterStatus;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "meters")
public class Meter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "meter_ref", nullable = false, unique = true, length = 100)
    private String meterRef;

    @Column(name = "initial_index_m3", nullable = false)
    private Double initialIndexM3;

    @Column(name = "installed_at")
    private LocalDate installedAt;

    @Column(name = "removed_at")
    private LocalDate removedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MeterStatus status = MeterStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "building_id", nullable = false)
    private Building building;

    public Meter() {
    }

    // --- Méthodes métier ---

    public void install() {
        this.status = MeterStatus.ACTIVE;
        if (this.installedAt == null) {
            this.installedAt = LocalDate.now();
        }
    }

    public void remove() {
        this.status = MeterStatus.REPLACED;
        if (this.removedAt == null) {
            this.removedAt = LocalDate.now();
        }
    }

    // --- Getters et Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMeterRef() {
        return meterRef;
    }

    public void setMeterRef(String meterRef) {
        this.meterRef = meterRef;
    }

    public Double getInitialIndexM3() {
        return initialIndexM3;
    }

    public void setInitialIndexM3(Double initialIndexM3) {
        this.initialIndexM3 = initialIndexM3;
    }

    public LocalDate getInstalledAt() {
        return installedAt;
    }

    public void setInstalledAt(LocalDate installedAt) {
        this.installedAt = installedAt;
    }

    public LocalDate getRemovedAt() {
        return removedAt;
    }

    public void setRemovedAt(LocalDate removedAt) {
        this.removedAt = removedAt;
    }

    public MeterStatus getStatus() {
        return status;
    }

    public void setStatus(MeterStatus status) {
        this.status = status;
    }

    public Building getBuilding() {
        return building;
    }

    public void setBuilding(Building building) {
        this.building = building;
    }
}