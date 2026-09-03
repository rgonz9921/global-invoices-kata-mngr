package com.project_kata.global_invoices_kata_mngr.infrastructure;

import com.project_kata.global_invoices_kata_mngr.domain.dto.CalculationResponse;
import com.project_kata.global_invoices_kata_mngr.domain.model.InvoiceType;
import com.project_kata.global_invoices_kata_mngr.domain.model.TypeRoleUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class InvoiceCalculationIntegrationTest extends AbstractIntegrationTest {

    private String bearer;

    @BeforeEach
    void loginAsOperador() throws Exception {
        bearer = seedAndLogin(OPERADOR_EMAIL, OPERADOR_PASSWORD, TypeRoleUser.OPERADOR);
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
