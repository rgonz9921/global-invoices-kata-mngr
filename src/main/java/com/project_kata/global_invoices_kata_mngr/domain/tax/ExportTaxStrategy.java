package com.project_kata.global_invoices_kata_mngr.domain.tax;

import com.project_kata.global_invoices_kata_mngr.domain.model.InvoiceTotals;
import com.project_kata.global_invoices_kata_mngr.domain.model.InvoiceType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/** Exportacion: Subtotal + 0% IVA (libre de impuestos). */
@Component
public class ExportTaxStrategy implements TaxStrategy {

    @Override
    public InvoiceType getSupportedType() {
        return InvoiceType.EXPORTACION;
    }

    @Override
    public InvoiceTotals calculate(BigDecimal subtotal) {
        Money.requireNonNegative(subtotal);
        BigDecimal base = Money.round(subtotal);
        BigDecimal zero = Money.round(BigDecimal.ZERO);
        return new InvoiceTotals(base, zero, zero, base);
    }
}
