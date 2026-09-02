package com.project_kata.global_invoices_kata_mngr.domain.dto;

import com.project_kata.global_invoices_kata_mngr.domain.model.InvoiceType;
import com.project_kata.global_invoices_kata_mngr.domain.validation.ConsistentCustomsCode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@ConsistentCustomsCode
public record CreateInvoiceRequest(

        @NotNull(message = "El tipo de factura es obligatorio")
        InvoiceType type,

        @NotBlank(message = "El concepto es obligatorio")
        @Size(max = 200, message = "El concepto no puede superar los 200 caracteres")
        String concepto,

        @NotNull(message = "El subtotal es obligatorio")
        @Positive(message = "El subtotal debe ser mayor que cero")
        BigDecimal subtotal,

        String codigoAduanero
) {
}
