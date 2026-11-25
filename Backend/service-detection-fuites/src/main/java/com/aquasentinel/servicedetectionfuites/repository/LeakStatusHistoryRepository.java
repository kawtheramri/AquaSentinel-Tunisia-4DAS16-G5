package com.aquasentinel.servicedetectionfuites.repository;

import com.aquasentinel.servicedetectionfuites.model.LeakStatusHistory;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface LeakStatusHistoryRepository extends MongoRepository<LeakStatusHistory, String> {
    List<LeakStatusHistory> findByLeakDetectionIdOrderByChangedAtAsc(String leakDetectionId);
    void deleteByLeakDetectionId(String leakDetectionId);
}
