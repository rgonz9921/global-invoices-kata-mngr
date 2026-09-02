package com.project_kata.global_invoices_kata_mngr.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuracion del firmado JWT. Los valores llegan por variables de entorno
 * ({@code JWT_SECRET}, {@code JWT_EXPIRATION_MS}) — nunca versionados.
 * @param secret
 * @param expirationMs
 */
@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(String secret, long expirationMs) {
}
