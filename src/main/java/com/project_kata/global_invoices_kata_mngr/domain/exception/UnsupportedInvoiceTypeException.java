package com.project_kata.global_invoices_kata_mngr.domain.exception;

import com.project_kata.global_invoices_kata_mngr.domain.model.InvoiceType;

/** No hay ninguna {@code TaxStrategy} registrada para el tipo de factura solicitado. */
public class UnsupportedInvoiceTypeException extends RuntimeException {

    public UnsupportedInvoiceTypeException(InvoiceType type) {
        super("No hay estrategia tributaria para el tipo de factura: " + type);
    }
}
