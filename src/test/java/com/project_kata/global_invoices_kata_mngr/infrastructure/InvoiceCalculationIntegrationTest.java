package com.project_kata.global_invoices_kata_mngr.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project_kata.global_invoices_kata_mngr.domain.dto.CalculationResponse;
import com.project_kata.global_invoices_kata_mngr.domain.model.InvoiceType;
import com.project_kata.global_invoices_kata_mngr.domain.model.TypeRoleUser;
import com.project_kata.global_invoices_kata_mngr.domain.model.User;
import com.project_kata.global_invoices_kata_mngr.infrastructure.persistence.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class InvoiceCalculationIntegrationTest {

    private static final String EMAIL = "operador@globalinvoice.com";
    private static final String PASSWORD = "Operador123!";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ObjectMapper objectMapper;

    private String bearer;

    @BeforeEach
    void setUp() throws Exception {
        userRepository.deleteAll();
        userRepository.save(User.builder()
                .name("Operador Demo").email(EMAIL)
                .password(passwordEncoder.encode(PASSWORD))
                .role(TypeRoleUser.OPERADOR).build());

        MvcResult login = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"%s\",\"password\":\"%s\"}".formatted(EMAIL, PASSWORD)))
                .andExpect(status().isOk())
                .andReturn();
        bearer = "Bearer " + objectMapper.readTree(login.getResponse().getContentAsString())
                .get("accessToken").asText();
    }

    private ResultActions calculate(String body) throws Exception {
        return mockMvc.perform(post("/api/v1/invoices/calculate")
                .header("Authorization", bearer)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));
    }

    private CalculationResponse calculateOk(String body) throws Exception {
        MvcResult result = calculate(body).andExpect(status().isOk()).andReturn();
        return objectMapper.readValue(result.getResponse().getContentAsString(), CalculationResponse.class);
    }

    @Test
    void calculatesNacional() throws Exception {
        CalculationResponse response = calculateOk("{\"type\":\"NACIONAL\",\"subtotal\":1000}");

        assertThat(response.type()).isEqualTo(InvoiceType.NACIONAL);
        assertThat(response.totals().iva()).isEqualByComparingTo("190.00");
        assertThat(response.totals().retencion()).isEqualByComparingTo("0.00");
        assertThat(response.totals().total()).isEqualByComparingTo("1190.00");
    }

    @Test
    void calculatesExportacion() throws Exception {
        CalculationResponse response = calculateOk("{\"type\":\"EXPORTACION\",\"subtotal\":1000}");

        assertThat(response.totals().iva()).isEqualByComparingTo("0.00");
        assertThat(response.totals().total()).isEqualByComparingTo("1000.00");
    }

    @Test
    void calculatesGubernamental() throws Exception {
        CalculationResponse response = calculateOk("{\"type\":\"GUBERNAMENTAL\",\"subtotal\":1000}");

        assertThat(response.totals().iva()).isEqualByComparingTo("190.00");
        assertThat(response.totals().retencion()).isEqualByComparingTo("50.00");
        assertThat(response.totals().total()).isEqualByComparingTo("1140.00");
    }

    @Test
    void rejectsUnknownType() throws Exception {
        calculate("{\"type\":\"MARCIANA\",\"subtotal\":1000}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void rejectsNegativeSubtotal() throws Exception {
        calculate("{\"type\":\"NACIONAL\",\"subtotal\":-5}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields").isNotEmpty());
    }

    @Test
    void rejectsAnonymousRequest() throws Exception {
        mockMvc.perform(post("/api/v1/invoices/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"NACIONAL\",\"subtotal\":1000}"))
                .andExpect(status().isUnauthorized());
    }
}
