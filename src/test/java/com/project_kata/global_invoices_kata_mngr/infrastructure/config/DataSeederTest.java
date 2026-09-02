package com.project_kata.global_invoices_kata_mngr.infrastructure.config;

import com.project_kata.global_invoices_kata_mngr.domain.model.TypeRoleUser;
import com.project_kata.global_invoices_kata_mngr.domain.model.User;
import com.project_kata.global_invoices_kata_mngr.infrastructure.persistence.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DataSeederTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    private static SeedProperties props() {
        return new SeedProperties(true,
                new SeedProperties.SeedUser("Operador Demo", "operador@x.com", "Operador123!"),
                new SeedProperties.SeedUser("Auditor Demo", "auditor@x.com", "Auditor123!"));
    }

    @Test
    void createsBothDemoUsersWhenAbsentWithHashedPasswords() {
        DataSeeder seeder = new DataSeeder(userRepository, passwordEncoder, props());
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashed");

        seeder.run(null);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(2)).save(captor.capture());
        assertThat(captor.getAllValues())
                .extracting(User::getRole)
                .containsExactly(TypeRoleUser.OPERADOR, TypeRoleUser.AUDITOR);
        assertThat(captor.getAllValues())
                .allMatch(u -> u.getPassword().equals("hashed"));
    }

    @Test
    void isIdempotentWhenUsersAlreadyExist() {
        DataSeeder seeder = new DataSeeder(userRepository, passwordEncoder, props());
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        seeder.run(null);

        verify(userRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void skipsRoleWhenConfigIncomplete() {
        SeedProperties incomplete = new SeedProperties(true,
                new SeedProperties.SeedUser("Operador", null, null),
                null);
        DataSeeder seeder = new DataSeeder(userRepository, passwordEncoder, incomplete);

        seeder.run(null);

        verify(userRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }
}
