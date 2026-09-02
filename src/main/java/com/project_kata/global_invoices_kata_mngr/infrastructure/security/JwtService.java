package com.project_kata.global_invoices_kata_mngr.infrastructure.security;

import com.project_kata.global_invoices_kata_mngr.domain.model.User;
import com.project_kata.global_invoices_kata_mngr.infrastructure.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

/**
 * Emision y verificacion de access tokens JWT (HS256).
 * <p>El token lleva el email en {@code sub} y el rol en el claim {@code role},
 * de modo que la autorizacion no obliga a recargar el rol de la BD en cada request.
 * Usa la API moderna de jjwt 0.12 ({@code verifyWith}/{@code parseSignedClaims}).
 */
@Service
public class JwtService {

    public static final String ROLE_CLAIM = "role";

    private final SecretKey key;
    private final long expirationMs;

    public JwtService(JwtProperties properties) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(properties.secret()));
        this.expirationMs = properties.expirationMs();
    }

    public String generateToken(User user) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(user.getEmail())
                .claim(ROLE_CLAIM, user.getRole().name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(expirationMs)))
                .signWith(key)
                .compact();
    }

    /** Verifica firma y expiracion. Lanza {@link io.jsonwebtoken.JwtException} si el token no es valido. */
    public Jws<Claims> parse(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token);
    }

    public String extractUsername(String token) {
        return parse(token).getPayload().getSubject();
    }

    public String extractRole(String token) {
        return parse(token).getPayload().get(ROLE_CLAIM, String.class);
    }

    public long getExpirationSeconds() {
        return expirationMs / 1000;
    }
}
