package com.project_kata.global_invoices_kata_mngr.infrastructure.controller;

import com.project_kata.global_invoices_kata_mngr.domain.dto.CurrentUserResponse;
import com.project_kata.global_invoices_kata_mngr.domain.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    /** Datos del usuario autenticado (para que el frontend pinte menu/guards segun rol). */
    @GetMapping("/me")
    public CurrentUserResponse me(@AuthenticationPrincipal UserDetails principal) {
        return userService.getCurrent(principal.getUsername());
    }
}
