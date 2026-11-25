package com.aquasentinel.servicedetectionfuites.config;

import com.aquasentinel.servicedetectionfuites.model.DetectionMethod;
import com.aquasentinel.servicedetectionfuites.model.DetectionRule;
import com.aquasentinel.servicedetectionfuites.repository.DetectionRuleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalTime;
import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedRules(DetectionRuleRepository repo) {
        return args -> {
            if (repo.count() > 0) return;
            repo.saveAll(List.of(
                DetectionRule.builder().method(DetectionMethod.NIGHT_FLOW).buildingType("ALL")
                    .thresholdPct(30.0).nightStart(LocalTime.of(0, 0)).nightEnd(LocalTime.of(5, 0))
                    .minDurationHours(2).active(true).build(),
                DetectionRule.builder().method(DetectionMethod.CONTINUOUS_FLOW).buildingType("ALL")
                    .thresholdPct(20.0).minDurationHours(24).active(true).build(),
                DetectionRule.builder().method(DetectionMethod.SUDDEN_PEAK).buildingType("ALL")
                    .thresholdPct(100.0).minDurationHours(1).active(true).build()
            ));
            System.out.println(">>> 3 règles de détection créées");
        };
    }
}
