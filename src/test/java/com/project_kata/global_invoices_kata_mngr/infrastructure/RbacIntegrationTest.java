package com.project_kata.global_invoices_kata_mngr.infrastructure;

import com.project_kata.global_invoices_kata_mngr.domain.model.TypeRoleUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RbacIntegrationTest extends AbstractIntegrationTest {

    private static final String VALID_INVOICE =
            "{\"type\":\"NACIONAL\",\"concepto\":\"Consultoria\",\"subtotal\":1000}";

    private String operador;
    private String auditor;

    @BeforeEach
    void logins() throws Exception {
        operador = seedAndLogin(OPERADOR_EMAIL, OPERADOR_PASSWORD, TypeRoleUser.OPERADOR);
        auditor = seedAndLogin(AUDITOR_EMAIL, AUDITOR_PASSWORD, TypeRoleUser.AUDITOR);
    }

    private static MockHttpServletRequestBuilder json(MockHttpServletRequestBuilder builder, String body) {
        return builder.contentType(MediaType.APPLICATION_JSON).content(body);
    }

    @Test
    void auditorCannotCreateInvoice() throws Exception {
        mockMvc.perform(json(post("/api/v1/invoices"), VALID_INVOICE).header("Authorization", auditor))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    void operatorCannotAccessDashboard() throws Exception {
        mockMvc.perform(get("/api/v1/dashboard/summary").header("Authorization", operador))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    void auditorCannotPreviewTotals() throws Exception {
        mockMvc.perform(json(post("/api/v1/invoices/calculate"), VALID_INVOICE).header("Authorization", auditor))
                .andExpect(status().isForbidden());
    }

    // --- Casos permitidos ---

    @Test
    void operatorCanCreateInvoice() throws Exception {
        mockMvc.perform(json(post("/api/v1/invoices"), VALID_INVOICE).header("Authorization", operador))
                .andExpect(status().isCreated());
    }

    @Test
    void operatorCanPreviewTotals() throws Exception {
        mockMvc.perform(json(post("/api/v1/invoices/calculate"), VALID_INVOICE).header("Authorization", operador))
                .andExpect(status().isOk());
    }

    @Test
    void bothRolesCanListInvoices() throws Exception {
        mockMvc.perform(get("/api/v1/invoices").header("Authorization", operador))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/invoices").header("Authorization", auditor))
                .andExpect(status().isOk());
    }

    @Test
    void bothRolesCanReadInvoiceDetail() throws Exception {
        String body = mockMvc.perform(json(post("/api/v1/invoices"), VALID_INVOICE)
                        .header("Authorization", operador))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        String id = objectMapper.readTree(body).get("id").asText();

        mockMvc.perform(get("/api/v1/invoices/{id}", id).header("Authorization", auditor))
                .andExpect(status().isOk());
    }

    @Test
    void auditorPassesDashboardSecurityButEndpointIsNotYetImplemented() throws Exception {
        mockMvc.perform(get("/api/v1/dashboard/summary").header("Authorization", auditor))
                .andExpect(status().isNotFound());
    }

    @Test
    void bothRolesCanReadOwnProfile() throws Exception {
        mockMvc.perform(get("/api/v1/users/me").header("Authorization", operador))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("OPERADOR"));
        mockMvc.perform(get("/api/v1/users/me").header("Authorization", auditor))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("AUDITOR"));
    }

    @Test
    void unauthenticatedRequestIsRejected() throws Exception {
        mockMvc.perform(json(post("/api/v1/invoices"), VALID_INVOICE))
                .andExpect(status().isUnauthorized());
    }
}
