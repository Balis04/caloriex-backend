package com.example.caloriexbackend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Value("${server.servlet.session.cookie.name:CALORIEX_SESSION}")
    private String sessionCookieName;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("sessionCookie", new SecurityScheme()
                                .name(sessionCookieName)
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.COOKIE)));
    }

    @Bean
    public OpenApiCustomizer customGlobalHeaderOpenApiCustomizer() {
        return openApi -> {
            openApi.getPaths().forEach((path, pathItem) -> {
                if (!isPublicPath(path)) {
                    pathItem.readOperations().forEach(operation ->
                            operation.addSecurityItem(new SecurityRequirement().addList("sessionCookie"))
                    );
                }
            });
        };
    }

    private boolean isPublicPath(String path) {
        return path.equals("/") ||
                path.equals("/api/auth/login") ||
                path.equals("/api/auth/me") ||
                path.equals("/api/auth/csrf") ||
                path.startsWith("/swagger-ui") ||
                path.startsWith("/v3/api-docs");
    }
}
