package com.project_kata.global_invoices_kata_mngr.domain.tax;

import com.project_kata.global_invoices_kata_mngr.domain.model.InvoiceTotals;
import com.project_kata.global_invoices_kata_mngr.domain.model.InvoiceType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class GovernmentTaxStrategyTest {

    private final GovernmentTaxStrategy strategy = new GovernmentTaxStrategy();

    @Test
    void supportsGubernamental() {
        assertThat(strategy.getSupportedType()).isEqualTo(InvoiceType.GUBERNAMENTAL);
    }

    @Test
    void appliesIvaMinusWithholding() {
        InvoiceTotals totals = strategy.calculate(new BigDecimal("1000"));

        assertThat(totals.subtotal()).isEqualByComparingTo("1000.00");
        assertThat(totals.iva()).isEqualByComparingTo("190.00");
        assertThat(totals.retencion()).isEqualByComparingTo("50.00");
        assertThat(totals.total()).isEqualByComparingTo("1140.00");
    }
}
