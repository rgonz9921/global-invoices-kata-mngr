package com.project_kata.global_invoices_kata_mngr.infrastructure;

import com.project_kata.global_invoices_kata_mngr.domain.model.TypeRoleUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class InvoiceDetailIntegrationTest extends AbstractIntegrationTest {

    private String bearer;

    @BeforeEach
    void loginAsOperador() throws Exception {
        bearer = seedAndLogin(OPERADOR_EMAIL, OPERADOR_PASSWORD, TypeRoleUser.OPERADOR);
    }

    private String createInvoice() throws Exception {
        String body = mockMvc.perform(post("/api/v1/invoices")
                        .header("Authorization", bearer)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"NACIONAL\",\"concepto\":\"Consultoria\",\"subtotal\":1000}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(body).get("id").asText();
    }

    @Test
    void detailIncludesMontoEnLetrasFromTheLegacyService() throws Exception {
        when(numberToTextConverter.toText(any(BigDecimal.class)))
                .thenReturn(Optional.of("one thousand one hundred and ninety"));
        String id = createInvoice();

        mockMvc.perform(get("/api/v1/invoices/{id}", id).header("Authorization", bearer))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totals.total").value(1190.00))
                .andExpect(jsonPath("$.montoEnLetras").value("one thousand one hundred and ninety"))
                .andExpect(jsonPath("$.conversionLetrasDisponible").value(true));
    }

    @Test
    void detailStillRespondsWhenTheLegacyServiceIsDown() throws Exception {
        when(numberToTextConverter.toText(any(BigDecimal.class))).thenReturn(Optional.empty());
        String id = createInvoice();

        mockMvc.perform(get("/api/v1/invoices/{id}", id).header("Authorization", bearer))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totals.total").value(1190.00))
                .andExpect(jsonPath("$.conversionLetrasDisponible").value(false))
                .andExpect(jsonPath("$.montoEnLetras").doesNotExist());
    }
}
