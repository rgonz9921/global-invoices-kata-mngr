package com.project_kata.global_invoices_kata_mngr.infrastructure;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project_kata.global_invoices_kata_mngr.domain.model.TypeRoleUser;
import com.project_kata.global_invoices_kata_mngr.domain.model.User;
import com.project_kata.global_invoices_kata_mngr.infrastructure.persistence.InvoiceRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class InvoicePersistenceIntegrationTest {

    private static final String EMAIL = "operador@globalinvoice.com";
    private static final String PASSWORD = "Operador123!";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private InvoiceRepository invoiceRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ObjectMapper objectMapper;

    private String bearer;

    @BeforeEach
    void setUp() throws Exception {
        invoiceRepository.deleteAll();
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

    private ResultActions create(String body) throws Exception {
        return mockMvc.perform(post("/api/v1/invoices")
                .header("Authorization", bearer)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));
    }

    private JsonNode createOk(String body) throws Exception {
        MvcResult result = create(body).andExpect(status().isCreated()).andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString());
    }

    @Test
    void createsNacionalInvoiceWithComputedTotalsAndAudit() throws Exception {
        JsonNode invoice = createOk("{\"type\":\"NACIONAL\",\"concepto\":\"Consultoria mensual\",\"subtotal\":1000}");

        assertThat(invoice.get("id").asText()).isNotBlank();
        assertThat(invoice.get("concepto").asText()).isEqualTo("Consultoria mensual");
        assertThat(invoice.path("totals").path("total").asDouble()).isEqualTo(1190.00);
        assertThat(invoice.get("createdBy").asText()).isEqualTo(EMAIL);
        assertThat(invoice.get("createdAt").isNull()).isFalse();
        assertThat(invoiceRepository.count()).isEqualTo(1);
    }

    @Test
    void rejectsBlankConcepto() throws Exception {
        create("{\"type\":\"NACIONAL\",\"concepto\":\"  \",\"subtotal\":1000}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields.concepto").isNotEmpty());
    }

    @Test
    void rejectsExportacionWithoutCustomsCode() throws Exception {
        create("{\"type\":\"EXPORTACION\",\"concepto\":\"Exportacion cafe\",\"subtotal\":1000}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields.codigoAduanero").isNotEmpty());
    }

    @Test
    void acceptsExportacionWithCustomsCode() throws Exception {
        JsonNode invoice = createOk(
                "{\"type\":\"EXPORTACION\",\"concepto\":\"Exportacion cafe\",\"subtotal\":1000,\"codigoAduanero\":\"COL-12345\"}");

        assertThat(invoice.get("codigoAduanero").asText()).isEqualTo("COL-12345");
    }

    @Test
    void rejectsNonExportacionWithCustomsCode() throws Exception {
        create("{\"type\":\"NACIONAL\",\"concepto\":\"Consultoria\",\"subtotal\":1000,\"codigoAduanero\":\"COL-1\"}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields.codigoAduanero").isNotEmpty());
    }

    @Test
    void listsAndFiltersByType() throws Exception {
        createOk("{\"type\":\"NACIONAL\",\"concepto\":\"Consultoria\",\"subtotal\":1000}");
        createOk("{\"type\":\"GUBERNAMENTAL\",\"concepto\":\"Contrato estatal\",\"subtotal\":2000}");

        mockMvc.perform(get("/api/v1/invoices").header("Authorization", bearer))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.content.length()").value(2));

        mockMvc.perform(get("/api/v1/invoices").header("Authorization", bearer).param("type", "GUBERNAMENTAL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].type").value("GUBERNAMENTAL"));
    }

    @Test
    void getsInvoiceByIdAnd404sForUnknown() throws Exception {
        String id = createOk("{\"type\":\"NACIONAL\",\"concepto\":\"Consultoria\",\"subtotal\":1000}")
                .get("id").asText();

        mockMvc.perform(get("/api/v1/invoices/{id}", id).header("Authorization", bearer))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));

        mockMvc.perform(get("/api/v1/invoices/{id}", "000000000000000000000000").header("Authorization", bearer))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void rejectsAnonymousCreate() throws Exception {
        mockMvc.perform(post("/api/v1/invoices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"NACIONAL\",\"subtotal\":1000}"))
                .andExpect(status().isUnauthorized());
    }
}
