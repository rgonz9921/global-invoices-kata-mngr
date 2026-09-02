package com.project_kata.global_invoices_kata_mngr.domain.service;

import com.project_kata.global_invoices_kata_mngr.domain.dto.LoginRequest;
import com.project_kata.global_invoices_kata_mngr.domain.dto.LoginResponse;

public interface IAuthService {

    /**
     * Valida credenciales y emite un access token JWT.
     *
     * @throws org.springframework.security.authentication.BadCredentialsException si el email o la contrasena no coinciden
     */
    LoginResponse login(LoginRequest request);
}
