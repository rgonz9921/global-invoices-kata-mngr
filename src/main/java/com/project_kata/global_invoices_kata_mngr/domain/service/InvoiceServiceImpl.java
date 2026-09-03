package com.project_kata.global_invoices_kata_mngr.domain.service;

import com.project_kata.global_invoices_kata_mngr.domain.dto.CreateInvoiceRequest;
import com.project_kata.global_invoices_kata_mngr.domain.dto.InvoiceDetailResponse;
import com.project_kata.global_invoices_kata_mngr.domain.dto.InvoiceResponse;
import com.project_kata.global_invoices_kata_mngr.domain.dto.PageResponse;
import com.project_kata.global_invoices_kata_mngr.domain.exception.InvoiceNotFoundException;
import com.project_kata.global_invoices_kata_mngr.domain.model.Invoice;
import com.project_kata.global_invoices_kata_mngr.domain.model.InvoiceTotals;
import com.project_kata.global_invoices_kata_mngr.domain.model.InvoiceType;
import com.project_kata.global_invoices_kata_mngr.domain.port.NumberToTextConverter;
import com.project_kata.global_invoices_kata_mngr.domain.tax.TaxStrategyFactory;
import com.project_kata.global_invoices_kata_mngr.infrastructure.persistence.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements IInvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final TaxStrategyFactory taxStrategyFactory;
    private final NumberToTextConverter numberToTextConverter;

    @Override
    public InvoiceResponse create(CreateInvoiceRequest request) {
        InvoiceTotals totals = taxStrategyFactory.getStrategy(request.type()).calculate(request.subtotal());

        Invoice invoice = Invoice.builder()
                .type(request.type())
                .description(request.description().trim())
                .subtotal(request.subtotal())
                .customsCode(normalize(request.customsCode()))
                .totals(totals)
                .build();

        return InvoiceResponse.from(invoiceRepository.save(invoice));
    }

    @Override
    public PageResponse<InvoiceResponse> list(InvoiceType type, Pageable pageable) {
        Page<Invoice> page = (type == null)
                ? invoiceRepository.findAll(pageable)
                : invoiceRepository.findByType(type, pageable);
        return PageResponse.of(page, InvoiceResponse::from);
    }

    @Override
    public InvoiceDetailResponse getDetail(String id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new InvoiceNotFoundException(id));
        String amountInWords = numberToTextConverter.toText(invoice.getTotals().total()).orElse(null);
        return InvoiceDetailResponse.from(invoice, amountInWords);
    }

    private static String normalize(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }
}
