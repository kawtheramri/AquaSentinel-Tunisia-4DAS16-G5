package com.aquasentinel.servicedetectionfuites.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "leak_status_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeakStatusHistory {

    @Id
    private String id;
    @Indexed
    private String leakDetectionId;       // ASSOCIATION → LeakDetection (* → 1)
    private DetectionStatus oldStatus;
    private DetectionStatus newStatus;
    private LocalDateTime changedAt;
    private String reason;
}
