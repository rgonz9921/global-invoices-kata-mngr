package com.project_kata.global_invoices_kata_mngr.domain.dto;

import com.project_kata.global_invoices_kata_mngr.domain.model.InvoiceType;

import java.math.BigDecimal;

public record InvoiceTypeSummary(InvoiceType type, BigDecimal totalAmount, long invoiceCount) {
}
