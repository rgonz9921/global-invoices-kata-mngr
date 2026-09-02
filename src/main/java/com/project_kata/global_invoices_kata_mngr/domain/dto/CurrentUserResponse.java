package com.project_kata.global_invoices_kata_mngr.domain.dto;

import com.project_kata.global_invoices_kata_mngr.domain.model.TypeRoleUser;
import com.project_kata.global_invoices_kata_mngr.domain.model.User;

public record CurrentUserResponse(
        String id,
        String name,
        String email,
        TypeRoleUser role
) {
    public static CurrentUserResponse from(User user) {
        return new CurrentUserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole());
    }
}
