package com.aquasentinel.servicedetectionfuites.repository;

import com.aquasentinel.servicedetectionfuites.model.DetectionMethod;
import com.aquasentinel.servicedetectionfuites.model.DetectionRule;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface DetectionRuleRepository extends MongoRepository<DetectionRule, String> {
    Optional<DetectionRule> findFirstByMethodAndActiveTrue(DetectionMethod method);
}
