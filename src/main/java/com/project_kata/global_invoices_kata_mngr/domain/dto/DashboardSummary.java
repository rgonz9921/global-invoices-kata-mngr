package com.project_kata.global_invoices_kata_mngr.domain.dto;

import java.math.BigDecimal;
import java.util.List;

public record DashboardSummary(
        List<InvoiceTypeSummary> byType,
        BigDecimal grandTotal,
        long totalInvoices
) {
}
