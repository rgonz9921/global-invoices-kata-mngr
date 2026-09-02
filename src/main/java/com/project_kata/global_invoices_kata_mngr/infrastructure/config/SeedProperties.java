package com.project_kata.global_invoices_kata_mngr.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.seed")
public record SeedProperties(boolean enabled, SeedUser operador, SeedUser auditor) {

    public record SeedUser(String name, String email, String password) {
    }
}
