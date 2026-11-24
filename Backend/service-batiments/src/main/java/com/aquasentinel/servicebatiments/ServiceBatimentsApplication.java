package com.aquasentinel.servicebatiments;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
        "com.aquasentinel.servicebatiments",
        "entity",
        "repository",
        "service",
        "controller"
})
@EntityScan(basePackages = {"entity"})
@EnableJpaRepositories(basePackages = {"repository"})
public class ServiceBatimentsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceBatimentsApplication.class, args);
    }
}