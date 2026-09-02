package com.project_kata.global_invoices_kata_mngr.domain.dto;

import com.project_kata.global_invoices_kata_mngr.domain.model.InvoiceTotals;
import com.project_kata.global_invoices_kata_mngr.domain.model.InvoiceType;

public record CalculationResponse(InvoiceType type, InvoiceTotals totals) {
}
