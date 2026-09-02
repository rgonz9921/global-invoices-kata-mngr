package com.project_kata.global_invoices_kata_mngr.domain.tax;

import com.project_kata.global_invoices_kata_mngr.domain.model.InvoiceTotals;
import com.project_kata.global_invoices_kata_mngr.domain.model.InvoiceType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class ExportTaxStrategyTest {

    private final ExportTaxStrategy strategy = new ExportTaxStrategy();

    @Test
    void supportsExportacion() {
        assertThat(strategy.getSupportedType()).isEqualTo(InvoiceType.EXPORTACION);
    }

    @Test
    void appliesZeroIvaSoTotalEqualsSubtotal() {
        InvoiceTotals totals = strategy.calculate(new BigDecimal("1000"));

        assertThat(totals.subtotal()).isEqualByComparingTo("1000.00");
        assertThat(totals.iva()).isEqualByComparingTo("0.00");
        assertThat(totals.retencion()).isEqualByComparingTo("0.00");
        assertThat(totals.total()).isEqualByComparingTo("1000.00");
    }
}
