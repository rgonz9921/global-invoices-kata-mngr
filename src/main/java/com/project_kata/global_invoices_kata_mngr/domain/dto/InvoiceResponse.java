package com.project_kata.global_invoices_kata_mngr.domain.dto;

import com.project_kata.global_invoices_kata_mngr.domain.model.Invoice;
import com.project_kata.global_invoices_kata_mngr.domain.model.InvoiceTotals;
import com.project_kata.global_invoices_kata_mngr.domain.model.InvoiceType;

import java.math.BigDecimal;
import java.time.Instant;

public record InvoiceResponse(
        String id,
        InvoiceType type,
        String description,
        BigDecimal subtotal,
        String customsCode,
        InvoiceTotals totals,
        Instant createdAt,
        String createdBy
) {
    public static InvoiceResponse from(Invoice invoice) {
        return new InvoiceResponse(
                invoice.getId(),
                invoice.getType(),
                invoice.getDescription(),
                invoice.getSubtotal(),
                invoice.getCustomsCode(),
                invoice.getTotals(),
                invoice.getCreatedAt(),
                invoice.getCreatedBy());
    }
}
