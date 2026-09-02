package com.project_kata.global_invoices_kata_mngr.infrastructure.controller;

import com.project_kata.global_invoices_kata_mngr.domain.dto.CalculateInvoiceRequest;
import com.project_kata.global_invoices_kata_mngr.domain.dto.CalculationResponse;
import com.project_kata.global_invoices_kata_mngr.domain.dto.CreateInvoiceRequest;
import com.project_kata.global_invoices_kata_mngr.domain.dto.InvoiceResponse;
import com.project_kata.global_invoices_kata_mngr.domain.dto.PageResponse;
import com.project_kata.global_invoices_kata_mngr.domain.model.InvoiceType;
import com.project_kata.global_invoices_kata_mngr.domain.service.IInvoiceCalculationService;
import com.project_kata.global_invoices_kata_mngr.domain.service.IInvoiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final IInvoiceCalculationService invoiceCalculationService;
    private final IInvoiceService invoiceService;

    // TODO Inc.3: restringir a hasRole('OPERADOR')
    @PostMapping("/calculate")
    public CalculationResponse calculate(@Valid @RequestBody CalculateInvoiceRequest request) {
        return invoiceCalculationService.calculate(request);
    }

    // TODO Inc.3: restringir a hasRole('OPERADOR')
    @PostMapping
    public ResponseEntity<InvoiceResponse> create(@Valid @RequestBody CreateInvoiceRequest request,
                                                  UriComponentsBuilder uriBuilder) {
        InvoiceResponse created = invoiceService.create(request);
        var location = uriBuilder.path("/api/v1/invoices/{id}").build(created.id());
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    public PageResponse<InvoiceResponse> list(
            @RequestParam(required = false) InvoiceType type,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return invoiceService.list(type, pageable);
    }

    @GetMapping("/{id}")
    public InvoiceResponse getById(@PathVariable String id) {
        return invoiceService.getById(id);
    }
}
