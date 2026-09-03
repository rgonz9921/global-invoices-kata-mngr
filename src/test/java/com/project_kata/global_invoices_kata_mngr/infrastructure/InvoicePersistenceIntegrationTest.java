package com.project_kata.global_invoices_kata_mngr.infrastructure;

import com.fasterxml.jackson.databind.JsonNode;
import com.project_kata.global_invoices_kata_mngr.domain.model.TypeRoleUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class InvoicePersistenceIntegrationTest extends AbstractIntegrationTest {

    private static final String EMAIL = OPERADOR_EMAIL;

    private String bearer;

    @BeforeEach
    void loginAsOperador() throws Exception {
        bearer = seedAndLogin(OPERADOR_EMAIL, OPERADOR_PASSWORD, TypeRoleUser.OPERADOR);
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
