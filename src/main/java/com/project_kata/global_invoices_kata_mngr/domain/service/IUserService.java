package com.project_kata.global_invoices_kata_mngr.domain.service;

import com.project_kata.global_invoices_kata_mngr.domain.dto.CurrentUserResponse;

public interface IUserService {
    CurrentUserResponse getCurrent(String email);
}
