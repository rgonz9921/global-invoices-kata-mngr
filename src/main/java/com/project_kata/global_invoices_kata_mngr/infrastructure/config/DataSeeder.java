package com.project_kata.global_invoices_kata_mngr.infrastructure.config;

import com.project_kata.global_invoices_kata_mngr.domain.model.TypeRoleUser;
import com.project_kata.global_invoices_kata_mngr.domain.model.User;
import com.project_kata.global_invoices_kata_mngr.infrastructure.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Crea los usuarios demo OPERADOR y AUDITOR al arranque cuando {@code app.seed.enabled=true}
 * (perfil dev). Idempotente: no duplica si ya existen.
 */
@Component
@ConditionalOnProperty(prefix = "app.seed", name = "enabled", havingValue = "true")
@RequiredArgsConstructor
public class DataSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SeedProperties seedProperties;

    @Override
    public void run(ApplicationArguments args) {
        seed(seedProperties.operador(), TypeRoleUser.OPERADOR);
        seed(seedProperties.auditor(), TypeRoleUser.AUDITOR);
    }

    private void seed(SeedProperties.SeedUser config, TypeRoleUser role) {
        if (config == null || isBlank(config.email()) || isBlank(config.password())) {
            log.warn("Seed de {} omitido: configuracion incompleta", role);
            return;
        }
        if (userRepository.existsByEmail(config.email())) {
            log.info("Seed de {} omitido: {} ya existe", role, config.email());
            return;
        }
        User user = User.builder()
                .name(config.name())
                .email(config.email())
                .password(passwordEncoder.encode(config.password()))
                .role(role)
                .build();
        userRepository.save(user);
        log.info("Usuario {} creado: {}", role, config.email());
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
