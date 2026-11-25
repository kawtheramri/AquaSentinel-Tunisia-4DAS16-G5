package com.aquasentinel.servicedetectionfuites.repository;

import com.aquasentinel.servicedetectionfuites.model.ProcessedEvent;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProcessedEventRepository extends MongoRepository<ProcessedEvent, String> {
}
