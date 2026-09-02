package com.project_kata.global_invoices_kata_mngr.domain.exception;

public class InvoiceNotFoundException extends RuntimeException {

    public InvoiceNotFoundException(String id) {
        super("No existe una factura con id: " + id);
    }
}
