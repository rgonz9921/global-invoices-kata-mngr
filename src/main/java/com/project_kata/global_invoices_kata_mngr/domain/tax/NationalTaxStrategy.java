package com.project_kata.global_invoices_kata_mngr.domain.tax;

import com.project_kata.global_invoices_kata_mngr.domain.model.InvoiceTotals;
import com.project_kata.global_invoices_kata_mngr.domain.model.InvoiceType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/** Nacional: Subtotal + 19% IVA. */
@Component
public class NationalTaxStrategy implements TaxStrategy {

    private static final BigDecimal IVA_RATE = new BigDecimal("0.19");

    @Override
    public InvoiceType getSupportedType() {
        return InvoiceType.NACIONAL;
    }

    @Override
    public InvoiceTotals calculate(BigDecimal subtotal) {
        Money.requireNonNegative(subtotal);
        BigDecimal base = Money.round(subtotal);
        BigDecimal iva = Money.percentageOf(base, IVA_RATE);
        BigDecimal retencion = Money.round(BigDecimal.ZERO);
        BigDecimal total = Money.round(base.add(iva));
        return new InvoiceTotals(base, iva, retencion, total);
    }
}
