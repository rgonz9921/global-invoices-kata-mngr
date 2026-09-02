package com.project_kata.global_invoices_kata_mngr.infrastructure.controller;

import com.project_kata.global_invoices_kata_mngr.domain.dto.CalculateInvoiceRequest;
import com.project_kata.global_invoices_kata_mngr.domain.dto.CalculationResponse;
import com.project_kata.global_invoices_kata_mngr.domain.service.IInvoiceCalculationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final IInvoiceCalculationService invoiceCalculationService;

    // TODO Inc.3: restringir a hasRole('OPERADOR')
    @PostMapping("/calculate")
    public CalculationResponse calculate(@Valid @RequestBody CalculateInvoiceRequest request) {
        return invoiceCalculationService.calculate(request);
    }
}
