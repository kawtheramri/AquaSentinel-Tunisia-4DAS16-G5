package com.aquasentinel.servicedetectionfuites.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalTime;

@Document(collection = "detection_rules")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetectionRule {

    @Id
    private String id;
    private DetectionMethod method;
    private String buildingType;
    private Double thresholdPct;
    private LocalTime nightStart;
    private LocalTime nightEnd;
    private Integer minDurationHours;
    private boolean active;

    /** La consommation mesurée dépasse-t-elle le seuil de cette règle ? */
    public boolean matches(double measured, double expected) {
        if (expected <= 0) return measured > 0;
        double deviationPct = (measured - expected) / expected * 100;
        return deviationPct >= thresholdPct;
    }

    /** Augmente le seuil de 10 % après une fausse alerte. */
    public void adjustThreshold() {
        this.thresholdPct = this.thresholdPct * 1.10;
    }
}
