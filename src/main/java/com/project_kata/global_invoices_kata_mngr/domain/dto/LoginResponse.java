package com.project_kata.global_invoices_kata_mngr.domain.dto;

/**
 * Respuesta de un login exitoso.
 *
 * @param accessToken JWT firmado (HS256) con el email en {@code sub} y el rol en el claim {@code role}
 * @param tokenType   siempre {@code "Bearer"}
 * @param expiresIn   segundos de vigencia del {@code accessToken}
 */
public record LoginResponse(
        String accessToken,
        String tokenType,
        long expiresIn
) {
    public static LoginResponse bearer(String accessToken, long expiresInSeconds) {
        return new LoginResponse(accessToken, "Bearer", expiresInSeconds);
    }
}
