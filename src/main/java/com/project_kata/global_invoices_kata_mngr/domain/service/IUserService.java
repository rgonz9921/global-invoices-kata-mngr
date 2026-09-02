package com.project_kata.global_invoices_kata_mngr.domain.service;

import com.project_kata.global_invoices_kata_mngr.domain.dto.CurrentUserResponse;

public interface IUserService {

    /**
     * @throws org.springframework.security.core.userdetails.UsernameNotFoundException si el email no existe
     */
    CurrentUserResponse getCurrent(String email);
}
