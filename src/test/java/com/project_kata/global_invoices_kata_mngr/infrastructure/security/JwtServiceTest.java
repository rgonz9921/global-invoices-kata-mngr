package com.project_kata.global_invoices_kata_mngr.infrastructure.security;

import com.project_kata.global_invoices_kata_mngr.domain.model.TypeRoleUser;
import com.project_kata.global_invoices_kata_mngr.domain.model.User;
import com.project_kata.global_invoices_kata_mngr.infrastructure.config.JwtProperties;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    private static final String SECRET =
            "Z2xvYmFsLWludm9pY2UtdGVzdC1zaWduaW5nLWtleS1kby1ub3QtdXNlLWluLXByb2QtMDEyMzQ1Njc4OQ==";
    private static final String OTHER_SECRET =
            "YW5vdGhlci1kaXN0aW5jdC1zaWduaW5nLWtleS1mb3ItbmVnYXRpdmUtdGVzdHMtOTg3NjU0MzIxMA==";

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(new JwtProperties(SECRET, 3_600_000L));
    }

    private static User user(String email, TypeRoleUser role) {
        return User.builder().id("1").name("Test").email(email).password("hash").role(role).build();
    }

    @Test
    void generatesTokenWithSubjectAndRoleClaim() {
        String token = jwtService.generateToken(user("operador@x.com", TypeRoleUser.OPERADOR));

        assertThat(jwtService.extractUsername(token)).isEqualTo("operador@x.com");
        assertThat(jwtService.extractRole(token)).isEqualTo("OPERADOR");
    }

    @Test
    void exposesExpirationInSeconds() {
        assertThat(jwtService.getExpirationSeconds()).isEqualTo(3600L);
    }

    @Test
    void rejectsTokenSignedWithAnotherSecret() {
        JwtService foreignIssuer = new JwtService(new JwtProperties(OTHER_SECRET, 3_600_000L));
        String foreignToken = foreignIssuer.generateToken(user("a@x.com", TypeRoleUser.AUDITOR));

        assertThatThrownBy(() -> jwtService.parse(foreignToken)).isInstanceOf(JwtException.class);
    }

    @Test
    void rejectsExpiredToken() {
        JwtService expiredIssuer = new JwtService(new JwtProperties(SECRET, -1_000L));
        String expiredToken = expiredIssuer.generateToken(user("a@x.com", TypeRoleUser.OPERADOR));

        assertThatThrownBy(() -> jwtService.parse(expiredToken)).isInstanceOf(ExpiredJwtException.class);
    }

    @Test
    void rejectsMalformedToken() {
        assertThatThrownBy(() -> jwtService.extractUsername("clearly-not-a-jwt"))
                .isInstanceOf(JwtException.class);
    }
}
