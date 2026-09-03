package com.project_kata.global_invoices_kata_mngr.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "app.soap.number-conversion")
public record SoapNumberConversionProperties(
        String url,
        Duration connectTimeout,
        Duration readTimeout
) {
}
