package com.project_kata.global_invoices_kata_mngr.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuracion del firmado JWT. Los valores llegan por variables de entorno
 * ({@code JWT_SECRET}, {@code JWT_EXPIRATION_MS}) — nunca versionados.
 *
 * @param secret       clave HMAC en Base64 (minimo 256 bits para HS256)
 * @param expirationMs vigencia del access token en milisegundos
 */
@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(String secret, long expirationMs) {
}
