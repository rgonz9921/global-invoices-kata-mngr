package com.project_kata.global_invoices_kata_mngr.domain.service;

import com.project_kata.global_invoices_kata_mngr.domain.dto.CalculateInvoiceRequest;
import com.project_kata.global_invoices_kata_mngr.domain.dto.CalculationResponse;

public interface IInvoiceCalculationService {

    CalculationResponse calculate(CalculateInvoiceRequest request);
}
