package com.dimitar.financetracker.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Paste only the JWT token value below; 'Bearer ' prefix will be added automatically."
)
@OpenAPIDefinition(
        security = @SecurityRequirement(name = "bearerAuth")
)
public class OpenApiConfig {
    @Bean
    public OpenAPI myOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Personal Finance Tracker")
                        .version("1.0.0")
                        .description("A comprehensive personal finance management application with predictive insights and budget tracking capabilities"));
    }
}
