package com.aquasentinel.servicedetectionfuites.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "leak_verifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeakVerification {

    @Id
    private String id;
    @Indexed
    private String leakDetectionId;       // ASSOCIATION → LeakDetection (* → 1)
    private Long verifiedBy;              // réf. JWT (utilisateur)
    private DetectionStatus decision;
    private String comment;
    private LocalDateTime verifiedAt;
}
