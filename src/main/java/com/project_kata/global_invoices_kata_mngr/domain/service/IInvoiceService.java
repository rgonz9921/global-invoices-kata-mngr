package com.project_kata.global_invoices_kata_mngr.domain.service;

import com.project_kata.global_invoices_kata_mngr.domain.dto.CreateInvoiceRequest;
import com.project_kata.global_invoices_kata_mngr.domain.dto.InvoiceResponse;
import com.project_kata.global_invoices_kata_mngr.domain.dto.PageResponse;
import com.project_kata.global_invoices_kata_mngr.domain.model.InvoiceType;
import org.springframework.data.domain.Pageable;

public interface IInvoiceService {

    InvoiceResponse create(CreateInvoiceRequest request);

    PageResponse<InvoiceResponse> list(InvoiceType type, Pageable pageable);

    InvoiceResponse getById(String id);
}
