package com.aquasentinel.servicepiecesrechange.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI sparePartsOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("AquaSentinel Tunisia — Spare Parts Service API")
                        .description("API REST de gestion du catalogue, des stocks, des réservations et des statistiques des pièces de rechange.")
                        .version("1.0.0"));
    }
}
