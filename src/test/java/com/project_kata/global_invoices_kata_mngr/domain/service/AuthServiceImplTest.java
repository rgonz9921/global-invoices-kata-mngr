package com.project_kata.global_invoices_kata_mngr.domain.service;

import com.project_kata.global_invoices_kata_mngr.domain.dto.LoginRequest;
import com.project_kata.global_invoices_kata_mngr.domain.dto.LoginResponse;
import com.project_kata.global_invoices_kata_mngr.domain.model.TypeRoleUser;
import com.project_kata.global_invoices_kata_mngr.domain.model.User;
import com.project_kata.global_invoices_kata_mngr.infrastructure.persistence.UserRepository;
import com.project_kata.global_invoices_kata_mngr.infrastructure.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private org.springframework.security.authentication.AuthenticationManager authenticationManager;
    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void loginReturnsBearerTokenWhenCredentialsAreValid() {
        User user = User.builder().email("operador@x.com").role(TypeRoleUser.OPERADOR).password("hash").build();
        when(userRepository.findByEmail("operador@x.com")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("signed.jwt.token");
        when(jwtService.getExpirationSeconds()).thenReturn(3600L);

        LoginResponse response = authService.login(new LoginRequest("operador@x.com", "Operador123!"));

        assertThat(response.accessToken()).isEqualTo("signed.jwt.token");
        assertThat(response.tokenType()).isEqualTo("Bearer");
        assertThat(response.expiresIn()).isEqualTo(3600L);
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void loginThrowsWhenAuthenticationManagerRejectsCredentials() {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("bad"));

        assertThatThrownBy(() -> authService.login(new LoginRequest("operador@x.com", "wrong")))
                .isInstanceOf(BadCredentialsException.class);

        verifyNoInteractions(jwtService);
    }

    @Test
    void loginThrowsWhenUserDisappearsAfterAuthentication() {
        when(userRepository.findByEmail("ghost@x.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(new LoginRequest("ghost@x.com", "whatever")))
                .isInstanceOf(BadCredentialsException.class);

        verifyNoInteractions(jwtService);
    }
}
