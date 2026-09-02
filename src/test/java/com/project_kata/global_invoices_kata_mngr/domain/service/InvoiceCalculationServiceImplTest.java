package com.project_kata.global_invoices_kata_mngr.domain.service;

import com.project_kata.global_invoices_kata_mngr.domain.dto.CalculateInvoiceRequest;
import com.project_kata.global_invoices_kata_mngr.domain.dto.CalculationResponse;
import com.project_kata.global_invoices_kata_mngr.domain.model.InvoiceTotals;
import com.project_kata.global_invoices_kata_mngr.domain.model.InvoiceType;
import com.project_kata.global_invoices_kata_mngr.domain.tax.TaxStrategy;
import com.project_kata.global_invoices_kata_mngr.domain.tax.TaxStrategyFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InvoiceCalculationServiceImplTest {

    @Mock
    private TaxStrategyFactory taxStrategyFactory;
    @Mock
    private TaxStrategy strategy;

    @InjectMocks
    private InvoiceCalculationServiceImpl service;

    @Test
    void delegatesToTheStrategyResolvedForTheRequestedType() {
        BigDecimal subtotal = new BigDecimal("500");
        InvoiceTotals totals = new InvoiceTotals(subtotal, new BigDecimal("95.00"),
                BigDecimal.ZERO, new BigDecimal("595.00"));
        when(taxStrategyFactory.getStrategy(InvoiceType.NACIONAL)).thenReturn(strategy);
        when(strategy.calculate(subtotal)).thenReturn(totals);

        CalculationResponse response = service.calculate(
                new CalculateInvoiceRequest(InvoiceType.NACIONAL, subtotal));

        assertThat(response.type()).isEqualTo(InvoiceType.NACIONAL);
        assertThat(response.totals()).isEqualTo(totals);
        verify(taxStrategyFactory).getStrategy(InvoiceType.NACIONAL);
        verify(strategy).calculate(subtotal);
    }
}
