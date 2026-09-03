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
import com.project_kata.global_invoices_kata_mngr.domain.tax.TaxStrategy;
import com.project_kata.global_invoices_kata_mngr.domain.tax.TaxStrategyFactory;
import com.project_kata.global_invoices_kata_mngr.infrastructure.persistence.InvoiceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InvoiceServiceImplTest {

    @Mock
    private InvoiceRepository invoiceRepository;
    @Mock
    private TaxStrategyFactory taxStrategyFactory;
    @Mock
    private TaxStrategy strategy;
    @Mock
    private NumberToTextConverter numberToTextConverter;

    @InjectMocks
    private InvoiceServiceImpl service;

    private final InvoiceTotals totals = new InvoiceTotals(
            new BigDecimal("1000.00"), new BigDecimal("190.00"),
            new BigDecimal("0.00"), new BigDecimal("1190.00"));

    private void stubStrategy(InvoiceType type, BigDecimal subtotal) {
        when(taxStrategyFactory.getStrategy(type)).thenReturn(strategy);
        when(strategy.calculate(subtotal)).thenReturn(totals);
        when(invoiceRepository.save(any(Invoice.class))).thenAnswer(i -> i.getArgument(0));
    }

    @Test
    void createComputesTotalsAndPersistsWithBlankCustomsCodeNormalizedToNull() {
        BigDecimal subtotal = new BigDecimal("1000");
        stubStrategy(InvoiceType.NACIONAL, subtotal);

        InvoiceResponse response = service.create(
                new CreateInvoiceRequest(InvoiceType.NACIONAL, "  Consultoria mensual  ", subtotal, "  "));

        ArgumentCaptor<Invoice> captor = ArgumentCaptor.forClass(Invoice.class);
        verify(invoiceRepository).save(captor.capture());
        Invoice saved = captor.getValue();
        assertThat(saved.getType()).isEqualTo(InvoiceType.NACIONAL);
        assertThat(saved.getDescription()).isEqualTo("Consultoria mensual");
        assertThat(saved.getTotals()).isEqualTo(totals);
        assertThat(saved.getCustomsCode()).isNull();
        assertThat(response.description()).isEqualTo("Consultoria mensual");
        assertThat(response.totals().total()).isEqualByComparingTo("1190.00");
    }

    @Test
    void createKeepsCustomsCodeForExportacion() {
        BigDecimal subtotal = new BigDecimal("1000");
        stubStrategy(InvoiceType.EXPORTACION, subtotal);

        service.create(new CreateInvoiceRequest(InvoiceType.EXPORTACION, "Exportacion cafe", subtotal, " COL-9 "));

        ArgumentCaptor<Invoice> captor = ArgumentCaptor.forClass(Invoice.class);
        verify(invoiceRepository).save(captor.capture());
        assertThat(captor.getValue().getCustomsCode()).isEqualTo("COL-9");
    }

    @Test
    void listWithoutTypeUsesFindAll() {
        Pageable pageable = PageRequest.of(0, 20);
        when(invoiceRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(sampleInvoice())));

        PageResponse<InvoiceResponse> page = service.list(null, pageable);

        assertThat(page.content()).hasSize(1);
        assertThat(page.totalElements()).isEqualTo(1);
        verify(invoiceRepository).findAll(pageable);
    }

    @Test
    void listWithTypeUsesFindByType() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<Invoice> result = new PageImpl<>(List.of(sampleInvoice()));
        when(invoiceRepository.findByType(InvoiceType.NACIONAL, pageable)).thenReturn(result);

        service.list(InvoiceType.NACIONAL, pageable);

        verify(invoiceRepository).findByType(InvoiceType.NACIONAL, pageable);
    }

    @Test
    void getDetailIncludesMontoEnLetrasWhenConverterSucceeds() {
        when(invoiceRepository.findById("abc")).thenReturn(Optional.of(sampleInvoice()));
        when(numberToTextConverter.toText(new BigDecimal("1190.00")))
                .thenReturn(Optional.of("one thousand one hundred and ninety"));

        InvoiceDetailResponse detail = service.getDetail("abc");

        assertThat(detail.id()).isEqualTo("abc");
        assertThat(detail.amountInWords()).isEqualTo("one thousand one hundred and ninety");
        assertThat(detail.amountInWordsAvailable()).isTrue();
    }

    @Test
    void getDetailFallsBackWhenConverterFails() {
        when(invoiceRepository.findById("abc")).thenReturn(Optional.of(sampleInvoice()));
        when(numberToTextConverter.toText(any(BigDecimal.class))).thenReturn(Optional.empty());

        InvoiceDetailResponse detail = service.getDetail("abc");

        assertThat(detail.amountInWords()).isNull();
        assertThat(detail.amountInWordsAvailable()).isFalse();
    }

    @Test
    void getDetailThrowsWhenMissing() {
        when(invoiceRepository.findById("nope")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getDetail("nope"))
                .isInstanceOf(InvoiceNotFoundException.class);
        verifyNoInteractions(numberToTextConverter);
    }

    private Invoice sampleInvoice() {
        return Invoice.builder()
                .id("abc").type(InvoiceType.NACIONAL).subtotal(new BigDecimal("1000"))
                .totals(totals).build();
    }
}
