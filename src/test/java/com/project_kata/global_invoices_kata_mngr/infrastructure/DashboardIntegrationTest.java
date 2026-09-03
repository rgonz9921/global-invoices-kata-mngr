package com.project_kata.global_invoices_kata_mngr.infrastructure;

import com.project_kata.global_invoices_kata_mngr.domain.dto.DashboardSummary;
import com.project_kata.global_invoices_kata_mngr.domain.dto.InvoiceTypeSummary;
import com.project_kata.global_invoices_kata_mngr.domain.model.InvoiceType;
import com.project_kata.global_invoices_kata_mngr.domain.model.TypeRoleUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DashboardIntegrationTest extends AbstractIntegrationTest {

    private String operador;
    private String auditor;

    @BeforeEach
    void logins() throws Exception {
        operador = seedAndLogin(OPERADOR_EMAIL, OPERADOR_PASSWORD, TypeRoleUser.OPERADOR);
        auditor = seedAndLogin(AUDITOR_EMAIL, AUDITOR_PASSWORD, TypeRoleUser.AUDITOR);
    }

    private void createInvoice(String type, int subtotal) throws Exception {
        String customsCode = "EXPORTACION".equals(type) ? ",\"customsCode\":\"COL-1\"" : "";
        mockMvc.perform(post("/api/v1/invoices")
                        .header("Authorization", operador)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"%s\",\"description\":\"x\",\"subtotal\":%d%s}"
                                .formatted(type, subtotal, customsCode)))
                .andExpect(status().isCreated());
    }

    private DashboardSummary summaryAsAuditor() throws Exception {
        String body = mockMvc.perform(get("/api/v1/dashboard/summary").header("Authorization", auditor))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(body, DashboardSummary.class);
    }

    private InvoiceTypeSummary forType(DashboardSummary summary, InvoiceType type) {
        return summary.byType().stream().filter(s -> s.type() == type).findFirst().orElseThrow();
    }

    @Test
    void aggregatesTotalAmountAndCountByType() throws Exception {
        createInvoice("NACIONAL", 1000);      // total 1190.00
        createInvoice("NACIONAL", 2000);      // total 2380.00
        createInvoice("EXPORTACION", 500);    // total 500.00
        createInvoice("GUBERNAMENTAL", 1000); // total 1140.00

        DashboardSummary summary = summaryAsAuditor();

        assertThat(summary.byType()).hasSize(3);
        assertThat(forType(summary, InvoiceType.NACIONAL).totalAmount()).isEqualByComparingTo("3570.00");
        assertThat(forType(summary, InvoiceType.NACIONAL).invoiceCount()).isEqualTo(2);
        assertThat(forType(summary, InvoiceType.EXPORTACION).totalAmount()).isEqualByComparingTo("500.00");
        assertThat(forType(summary, InvoiceType.EXPORTACION).invoiceCount()).isEqualTo(1);
        assertThat(forType(summary, InvoiceType.GUBERNAMENTAL).totalAmount()).isEqualByComparingTo("1140.00");
        assertThat(summary.grandTotal()).isEqualByComparingTo("5210.00");
        assertThat(summary.totalInvoices()).isEqualTo(4);
    }

    @Test
    void returnsAllThreeTypesAtZeroWhenThereAreNoInvoices() throws Exception {
        DashboardSummary summary = summaryAsAuditor();

        assertThat(summary.byType()).hasSize(3)
                .allSatisfy(s -> {
                    assertThat(s.totalAmount()).isEqualByComparingTo("0.00");
                    assertThat(s.invoiceCount()).isZero();
                });
        assertThat(summary.grandTotal()).isEqualByComparingTo("0.00");
        assertThat(summary.totalInvoices()).isZero();
    }

    @Test
    void operatorIsForbidden() throws Exception {
        mockMvc.perform(get("/api/v1/dashboard/summary").header("Authorization", operador))
                .andExpect(status().isForbidden());
    }
}
