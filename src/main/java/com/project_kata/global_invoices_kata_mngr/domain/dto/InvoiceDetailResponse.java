package com.project_kata.global_invoices_kata_mngr.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.project_kata.global_invoices_kata_mngr.domain.model.Invoice;
import com.project_kata.global_invoices_kata_mngr.domain.model.InvoiceTotals;
import com.project_kata.global_invoices_kata_mngr.domain.model.InvoiceType;

import java.math.BigDecimal;
import java.time.Instant;

/** Detalle de factura persistida + el total en letras del servicio legacy. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record InvoiceDetailResponse(
        String id,
        InvoiceType type,
        String concepto,
        BigDecimal subtotal,
        String codigoAduanero,
        InvoiceTotals totals,
        Instant createdAt,
        String createdBy,
        String montoEnLetras,
        boolean conversionLetrasDisponible
) {
    public static InvoiceDetailResponse from(Invoice invoice, String montoEnLetras) {
        return new InvoiceDetailResponse(
                invoice.getId(),
                invoice.getType(),
                invoice.getConcepto(),
                invoice.getSubtotal(),
                invoice.getCodigoAduanero(),
                invoice.getTotals(),
                invoice.getCreatedAt(),
                invoice.getCreatedBy(),
                montoEnLetras,
                montoEnLetras != null);
    }
}
