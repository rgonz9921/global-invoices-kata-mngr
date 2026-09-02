package com.project_kata.global_invoices_kata_mngr.domain.service;

import com.project_kata.global_invoices_kata_mngr.domain.model.TypeRoleUser;
import com.project_kata.global_invoices_kata_mngr.domain.model.User;
import com.project_kata.global_invoices_kata_mngr.infrastructure.persistence.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsServiceImpl service;

    @Test
    void mapsDomainRoleToPrefixedAuthority() {
        when(userRepository.findByEmail("auditor@x.com")).thenReturn(Optional.of(
                User.builder().email("auditor@x.com").password("hash").role(TypeRoleUser.AUDITOR).build()));

        UserDetails details = service.loadUserByUsername("auditor@x.com");

        assertThat(details.getUsername()).isEqualTo("auditor@x.com");
        assertThat(details.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly("ROLE_AUDITOR");
    }

    @Test
    void throwsWhenEmailNotFound() {
        when(userRepository.findByEmail("nobody@x.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.loadUserByUsername("nobody@x.com"))
                .isInstanceOf(UsernameNotFoundException.class);
    }
}
