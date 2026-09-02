package com.project_kata.global_invoices_kata_mngr.domain.tax;

import com.project_kata.global_invoices_kata_mngr.domain.model.InvoiceTotals;
import com.project_kata.global_invoices_kata_mngr.domain.model.InvoiceType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NationalTaxStrategyTest {

    private final NationalTaxStrategy strategy = new NationalTaxStrategy();

    @Test
    void supportsNacional() {
        assertThat(strategy.getSupportedType()).isEqualTo(InvoiceType.NACIONAL);
    }

    @Test
    void appliesNineteenPercentIva() {
        InvoiceTotals totals = strategy.calculate(new BigDecimal("100"));

        assertThat(totals.subtotal()).isEqualByComparingTo("100.00");
        assertThat(totals.iva()).isEqualByComparingTo("19.00");
        assertThat(totals.retencion()).isEqualByComparingTo("0.00");
        assertThat(totals.total()).isEqualByComparingTo("119.00");
    }

    @Test
    void roundsIvaHalfUp() {
        InvoiceTotals totals = strategy.calculate(new BigDecimal("100.05"));

        assertThat(totals.iva()).isEqualByComparingTo("19.01");
        assertThat(totals.total()).isEqualByComparingTo("119.06");
    }

    @Test
    void handlesZeroSubtotal() {
        InvoiceTotals totals = strategy.calculate(BigDecimal.ZERO);

        assertThat(totals.iva()).isEqualByComparingTo("0.00");
        assertThat(totals.total()).isEqualByComparingTo("0.00");
    }

    @Test
    void rejectsNegativeSubtotal() {
        assertThatThrownBy(() -> strategy.calculate(new BigDecimal("-1")))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
