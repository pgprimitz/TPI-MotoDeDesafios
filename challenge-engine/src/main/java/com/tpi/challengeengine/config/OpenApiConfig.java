package com.tpi.challengeengine.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Define la informacion general publicada en la especificacion OpenAPI.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI challengeEngineOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Challenge Engine - T03")
                .description("Motor de Desafios de la plataforma educativa gamificada.")
                .version("0.1.0"));
    }
}
