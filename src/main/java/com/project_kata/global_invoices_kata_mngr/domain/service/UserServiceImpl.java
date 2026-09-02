package com.project_kata.global_invoices_kata_mngr.domain.service;

import com.project_kata.global_invoices_kata_mngr.domain.dto.CurrentUserResponse;
import com.project_kata.global_invoices_kata_mngr.infrastructure.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;

    @Override
    public CurrentUserResponse getCurrent(String email) {
        return userRepository.findByEmail(email)
                .map(CurrentUserResponse::from)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));
    }
}
