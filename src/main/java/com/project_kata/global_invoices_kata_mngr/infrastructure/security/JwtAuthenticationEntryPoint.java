package com.project_kata.global_invoices_kata_mngr.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project_kata.global_invoices_kata_mngr.domain.dto.ApiError;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(jakarta.servlet.http.HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        writeError(objectMapper, response, HttpStatus.UNAUTHORIZED,
                "No autenticado: se requiere un token JWT valido");
    }

    static void writeError(ObjectMapper objectMapper, HttpServletResponse response,
                           HttpStatus status, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        ApiError body = ApiError.of(status.value(), status.getReasonPhrase(), message);
        objectMapper.writeValue(response.getWriter(), body);
    }
}
