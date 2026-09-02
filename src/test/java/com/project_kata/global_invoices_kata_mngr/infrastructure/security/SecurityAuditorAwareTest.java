package com.project_kata.global_invoices_kata_mngr.infrastructure.security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityAuditorAwareTest {

    private final SecurityAuditorAware auditorAware = new SecurityAuditorAware();

    @AfterEach
    void clear() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void returnsAuthenticatedUsername() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("operador@globalinvoice.com", null, List.of()));

        assertThat(auditorAware.getCurrentAuditor()).contains("operador@globalinvoice.com");
    }

    @Test
    void returnsEmptyWhenNoAuthentication() {
        assertThat(auditorAware.getCurrentAuditor()).isEmpty();
    }
}
