package com.aquasentinel.servicedetectionfuites.repository;

import com.aquasentinel.servicedetectionfuites.model.LeakVerification;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface LeakVerificationRepository extends MongoRepository<LeakVerification, String> {
    List<LeakVerification> findByLeakDetectionId(String leakDetectionId);
    void deleteByLeakDetectionId(String leakDetectionId);
}
