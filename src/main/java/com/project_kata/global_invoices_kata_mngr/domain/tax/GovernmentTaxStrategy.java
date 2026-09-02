package com.project_kata.global_invoices_kata_mngr.domain.tax;

import com.project_kata.global_invoices_kata_mngr.domain.model.InvoiceTotals;
import com.project_kata.global_invoices_kata_mngr.domain.model.InvoiceType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/** Gubernamental: Subtotal + 19% IVA - 5% retencion en la fuente. */
@Component
public class GovernmentTaxStrategy implements TaxStrategy {

    private static final BigDecimal IVA_RATE = new BigDecimal("0.19");
    private static final BigDecimal RETENCION_RATE = new BigDecimal("0.05");

    @Override
    public InvoiceType getSupportedType() {
        return InvoiceType.GUBERNAMENTAL;
    }

    @Override
    public InvoiceTotals calculate(BigDecimal subtotal) {
        Money.requireNonNegative(subtotal);
        BigDecimal base = Money.round(subtotal);
        BigDecimal iva = Money.percentageOf(base, IVA_RATE);
        BigDecimal retencion = Money.percentageOf(base, RETENCION_RATE);
        BigDecimal total = Money.round(base.add(iva).subtract(retencion));
        return new InvoiceTotals(base, iva, retencion, total);
    }
}
