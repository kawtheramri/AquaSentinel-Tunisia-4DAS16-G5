
package com.aquasentinel.serviceinterventions;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(
        scanBasePackages = {
                "com.aquasentinel.serviceinterventions",
                "entity",
                "repository",
                "service",
                "controller"
        }
)
@EntityScan(basePackages = "entity")
@EnableJpaRepositories(basePackages = "repository")
public class ServiceInterventionsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceInterventionsApplication.class, args);
    }
}
