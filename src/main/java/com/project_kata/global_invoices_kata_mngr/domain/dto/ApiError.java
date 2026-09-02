package com.project_kata.global_invoices_kata_mngr.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.Map;

/**
 * Cuerpo de error uniforme para toda la API.
 *
 * @param status    codigo HTTP
 * @param error     frase de estado ("Unauthorized", "Bad Request"...)
 * @param message   mensaje legible, sin filtrar detalles internos
 * @param fields    errores por campo (solo en validaciones); null en el resto
 * @param timestamp momento del error
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiError(
        int status,
        String error,
        String message,
        Map<String, String> fields,
        Instant timestamp
) {
    public static ApiError of(int status, String error, String message) {
        return new ApiError(status, error, message, null, Instant.now());
    }

    public static ApiError of(int status, String error, String message, Map<String, String> fields) {
        return new ApiError(status, error, message, fields, Instant.now());
    }
}
