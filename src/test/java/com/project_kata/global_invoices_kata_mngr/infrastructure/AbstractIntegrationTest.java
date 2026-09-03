package com.project_kata.global_invoices_kata_mngr.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project_kata.global_invoices_kata_mngr.domain.model.TypeRoleUser;
import com.project_kata.global_invoices_kata_mngr.domain.model.User;
import com.project_kata.global_invoices_kata_mngr.domain.port.NumberToTextConverter;
import com.project_kata.global_invoices_kata_mngr.infrastructure.persistence.InvoiceRepository;
import com.project_kata.global_invoices_kata_mngr.infrastructure.persistence.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
abstract class AbstractIntegrationTest {

    protected static final String OPERADOR_EMAIL = "operador@globalinvoice.com";
    protected static final String OPERADOR_PASSWORD = "Operador123!";
    protected static final String AUDITOR_EMAIL = "auditor@globalinvoice.com";
    protected static final String AUDITOR_PASSWORD = "Auditor123!";

    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected InvoiceRepository invoiceRepository;
    @Autowired
    protected PasswordEncoder passwordEncoder;

    @MockitoBean
    protected NumberToTextConverter numberToTextConverter;

    @BeforeEach
    void resetDatabase() {
        invoiceRepository.deleteAll();
        userRepository.deleteAll();
        Mockito.lenient().when(numberToTextConverter.toText(any(BigDecimal.class)))
                .thenReturn(Optional.empty());
    }

    protected void seedUser(String email, String password, TypeRoleUser role) {
        userRepository.save(User.builder()
                .name(role.name() + " Demo")
                .email(email)
                .password(passwordEncoder.encode(password))
                .role(role)
                .build());
    }

    protected String login(String email, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"%s\",\"password\":\"%s\"}".formatted(email, password)))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString())
                .get("accessToken").asText();
    }

    protected String seedAndLogin(String email, String password, TypeRoleUser role) throws Exception {
        seedUser(email, password, role);
        return "Bearer " + login(email, password);
    }
}
