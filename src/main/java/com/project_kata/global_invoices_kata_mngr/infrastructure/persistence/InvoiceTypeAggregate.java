package com.project_kata.global_invoices_kata_mngr.infrastructure.persistence;

import java.math.BigDecimal;

public record InvoiceTypeAggregate(String id, BigDecimal totalAmount, long invoiceCount) {
}
