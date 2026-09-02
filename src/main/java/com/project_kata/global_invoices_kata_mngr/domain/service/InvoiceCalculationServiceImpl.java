package com.project_kata.global_invoices_kata_mngr.domain.service;

import com.project_kata.global_invoices_kata_mngr.domain.dto.CalculateInvoiceRequest;
import com.project_kata.global_invoices_kata_mngr.domain.dto.CalculationResponse;
import com.project_kata.global_invoices_kata_mngr.domain.model.InvoiceTotals;
import com.project_kata.global_invoices_kata_mngr.domain.tax.TaxStrategy;
import com.project_kata.global_invoices_kata_mngr.domain.tax.TaxStrategyFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InvoiceCalculationServiceImpl implements IInvoiceCalculationService {

    private final TaxStrategyFactory taxStrategyFactory;

    @Override
    public CalculationResponse calculate(CalculateInvoiceRequest request) {
        TaxStrategy strategy = taxStrategyFactory.getStrategy(request.type());
        InvoiceTotals totals = strategy.calculate(request.subtotal());
        return new CalculationResponse(request.type(), totals);
    }
}
