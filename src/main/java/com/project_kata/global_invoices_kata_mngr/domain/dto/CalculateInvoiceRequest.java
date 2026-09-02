package com.project_kata.global_invoices_kata_mngr.domain.dto;

import com.project_kata.global_invoices_kata_mngr.domain.model.InvoiceType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CalculateInvoiceRequest(

        @NotNull(message = "El tipo de factura es obligatorio")
        InvoiceType type,

        @NotNull(message = "El subtotal es obligatorio")
        @Positive(message = "El subtotal debe ser mayor que cero")
        BigDecimal subtotal
) {
}
