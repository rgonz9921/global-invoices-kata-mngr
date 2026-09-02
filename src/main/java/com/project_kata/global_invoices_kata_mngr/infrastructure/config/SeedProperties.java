package com.project_kata.global_invoices_kata_mngr.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Usuarios semilla creados al arranque cuando {@code app.seed.enabled=true} (solo dev).
 * Las contrasenas llegan por variables de entorno.
 */
@ConfigurationProperties(prefix = "app.seed")
public record SeedProperties(boolean enabled, SeedUser operador, SeedUser auditor) {

    public record SeedUser(String name, String email, String password) {
    }
}
