package com.project_kata.global_invoices_kata_mngr.domain.tax;

import com.project_kata.global_invoices_kata_mngr.domain.model.InvoiceTotals;
import com.project_kata.global_invoices_kata_mngr.domain.model.InvoiceType;

import java.math.BigDecimal;

public interface TaxStrategy {

    InvoiceType getSupportedType();

    InvoiceTotals calculate(BigDecimal subtotal);
}
