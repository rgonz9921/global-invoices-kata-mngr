package com.project_kata.global_invoices_kata_mngr.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * Origenes permitidos para CORS. En dev es {@code http://localhost:4200};
 * en prod, el dominio de Netlify — via {@code CORS_ALLOWED_ORIGINS}.
 */
@ConfigurationProperties(prefix = "app.cors")
public record CorsProperties(List<String> allowedOrigins) {
}
