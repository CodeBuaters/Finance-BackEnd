package com.example.finance.finance_backend.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

// Swagger UI is served at /swagger-ui.html and the raw spec at /v3/api-docs.
// Every endpoint requires a JWT by default; public endpoints opt out with
// @SecurityRequirements() in their controller.
@Configuration
public class OpenApiConfig {

    public static final String BEARER_AUTH = "bearerAuth";

    @Bean
    public OpenAPI finSightOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("FinSight API")
                        .version("v1")
                        .description("""
                                REST API for **FinSight**, a personal finance dashboard.

                                Users can import bank transactions from CSV files, have them categorized \
                                automatically, set monthly budgets per category and view spending summaries.

                                **How to authenticate in Swagger UI**
                                1. Register with `POST /api/auth/register` (or use an existing account).
                                2. Log in with `POST /api/auth/login` and copy the `accessToken` from the response.
                                3. Click **Authorize** at the top of the page and paste the token.
                                """))
                .components(new Components()
                        .addSecuritySchemes(BEARER_AUTH, new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Access token returned by POST /api/auth/login")))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_AUTH));
    }
}
