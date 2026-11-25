package com.aquasentinel.servicedetectionfuites.repository;

import com.aquasentinel.servicedetectionfuites.model.DetectionStatus;
import com.aquasentinel.servicedetectionfuites.model.LeakDetection;
import com.aquasentinel.servicedetectionfuites.model.RiskLevel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface LeakDetectionRepository extends MongoRepository<LeakDetection, String> {
    List<LeakDetection> findByBuildingId(Long buildingId);
    List<LeakDetection> findByStatus(DetectionStatus status);
    List<LeakDetection> findByRiskLevel(RiskLevel riskLevel);
    List<LeakDetection> findByRuleId(String ruleId);           // association règle → détections
    boolean existsByMeasurementId(String measurementId);
}
