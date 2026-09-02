package com.project_kata.global_invoices_kata_mngr.domain.service;

import com.project_kata.global_invoices_kata_mngr.domain.dto.CurrentUserResponse;
import com.project_kata.global_invoices_kata_mngr.domain.model.TypeRoleUser;
import com.project_kata.global_invoices_kata_mngr.domain.model.User;
import com.project_kata.global_invoices_kata_mngr.infrastructure.persistence.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void returnsCurrentUserWithoutPassword() {
        when(userRepository.findByEmail("operador@x.com")).thenReturn(Optional.of(
                User.builder().id("42").name("Operador Demo").email("operador@x.com")
                        .password("hash").role(TypeRoleUser.OPERADOR).build()));

        CurrentUserResponse response = userService.getCurrent("operador@x.com");

        assertThat(response).isEqualTo(
                new CurrentUserResponse("42", "Operador Demo", "operador@x.com", TypeRoleUser.OPERADOR));
    }

    @Test
    void throwsWhenUserNotFound() {
        when(userRepository.findByEmail("nobody@x.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getCurrent("nobody@x.com"))
                .isInstanceOf(UsernameNotFoundException.class);
    }
}
