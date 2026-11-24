package entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "baseline_profiles")
public class BaselineProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "baseline_daily_m3", nullable = false)
    private Double baselineDailyM3;

    @Column(length = 100)
    private String method;

    @Column(name = "valid_from")
    private LocalDate validFrom;

    @Column(name = "computed_at")
    private LocalDateTime computedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "building_id", nullable = false)
    private Building building;

    public BaselineProfile() {
    }

    public void computeBaseline(Double value, String methodName) {
        this.baselineDailyM3 = value;
        this.method = methodName;
        this.computedAt = LocalDateTime.now();
        if (this.validFrom == null) {
            this.validFrom = LocalDate.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getBaselineDailyM3() {
        return baselineDailyM3;
    }

    public void setBaselineDailyM3(Double baselineDailyM3) {
        this.baselineDailyM3 = baselineDailyM3;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public LocalDate getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(LocalDate validFrom) {
        this.validFrom = validFrom;
    }

    public LocalDateTime getComputedAt() {
        return computedAt;
    }

    public void setComputedAt(LocalDateTime computedAt) {
        this.computedAt = computedAt;
    }

    public Building getBuilding() {
        return building;
    }

    public void setBuilding(Building building) {
        this.building = building;
    }
}