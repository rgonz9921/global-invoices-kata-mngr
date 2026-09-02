package com.project_kata.global_invoices_kata_mngr.domain.model;

import java.math.BigDecimal;

public record InvoiceTotals(
        BigDecimal subtotal,
        BigDecimal iva,
        BigDecimal retencion,
        BigDecimal total
) {
}
