package com.project_kata.global_invoices_kata_mngr.infrastructure.controller;

import com.project_kata.global_invoices_kata_mngr.domain.dto.LoginRequest;
import com.project_kata.global_invoices_kata_mngr.domain.dto.LoginResponse;
import com.project_kata.global_invoices_kata_mngr.domain.service.IAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IAuthService authService;

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
}
