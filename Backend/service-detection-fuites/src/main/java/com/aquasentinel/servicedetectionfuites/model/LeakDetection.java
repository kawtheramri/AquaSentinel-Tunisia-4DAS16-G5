package com.aquasentinel.servicedetectionfuites.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "leak_detections")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeakDetection {

    @Id
    private String id;

    @Indexed
    private Long buildingId;              // réf. Building.id
    @Indexed(unique = true)
    private String measurementId;         // réf. WaterMeasurement.id (1 relevé = 1 analyse)
    private Long maintenanceRequestId;    // réf. Maintenance.id
    @Indexed
    private String ruleId;                // ASSOCIATION → DetectionRule (* → 1)

    private LocalDateTime detectedAt;
    private boolean isSimulated;
    private DetectionMethod method;
    private Double measuredValue;
    private Double expectedValue;
    private Double estimatedLossM3Day;
    private RiskLevel riskLevel;
    private DetectionStatus status;

    /** Calcule la perte estimée et le niveau de risque. */
    public void analyze() {
        this.estimatedLossM3Day = Math.max(0, measuredValue - expectedValue);
        double pct = expectedValue <= 0 ? 100 : (measuredValue - expectedValue) / expectedValue * 100;
        if (pct >= 100)      this.riskLevel = RiskLevel.CRITICAL;
        else if (pct >= 50)  this.riskLevel = RiskLevel.HIGH;
        else if (pct >= 25)  this.riskLevel = RiskLevel.MEDIUM;
        else                 this.riskLevel = RiskLevel.LOW;
        this.status = DetectionStatus.NEW;
    }

    public void verify()            { this.status = DetectionStatus.VERIFIED; }
    public void markFalsePositive() { this.status = DetectionStatus.FALSE_POSITIVE; }
    public void resolve()           { this.status = DetectionStatus.RESOLVED; }
}
