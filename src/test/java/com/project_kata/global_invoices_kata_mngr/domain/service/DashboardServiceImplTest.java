package com.project_kata.global_invoices_kata_mngr.domain.service;

import com.project_kata.global_invoices_kata_mngr.domain.dto.DashboardSummary;
import com.project_kata.global_invoices_kata_mngr.domain.dto.InvoiceTypeSummary;
import com.project_kata.global_invoices_kata_mngr.domain.model.InvoiceType;
import com.project_kata.global_invoices_kata_mngr.infrastructure.persistence.DashboardRepository;
import com.project_kata.global_invoices_kata_mngr.infrastructure.persistence.InvoiceTypeAggregate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceImplTest {

    @Mock
    private DashboardRepository dashboardRepository;

    @InjectMocks
    private DashboardServiceImpl service;

    private InvoiceTypeSummary summaryFor(DashboardSummary summary, InvoiceType type) {
        return summary.byType().stream().filter(s -> s.type() == type).findFirst().orElseThrow();
    }

    @Test
    void zeroFillsMissingTypesAndComputesTotals() {
        when(dashboardRepository.totalsByType()).thenReturn(List.of(
                new InvoiceTypeAggregate("NACIONAL", new BigDecimal("3570.00"), 2),
                new InvoiceTypeAggregate("GUBERNAMENTAL", new BigDecimal("1140.00"), 1)));

        DashboardSummary result = service.getSummary();

        assertThat(result.byType()).hasSize(3);
        assertThat(summaryFor(result, InvoiceType.NACIONAL).totalAmount()).isEqualByComparingTo("3570.00");
        assertThat(summaryFor(result, InvoiceType.NACIONAL).invoiceCount()).isEqualTo(2);
        assertThat(summaryFor(result, InvoiceType.EXPORTACION).totalAmount()).isEqualByComparingTo("0.00");
        assertThat(summaryFor(result, InvoiceType.EXPORTACION).invoiceCount()).isZero();
        assertThat(result.grandTotal()).isEqualByComparingTo("4710.00");
        assertThat(result.totalInvoices()).isEqualTo(3);
    }

    @Test
    void returnsAllZerosWhenThereAreNoInvoices() {
        when(dashboardRepository.totalsByType()).thenReturn(List.of());

        DashboardSummary result = service.getSummary();

        assertThat(result.byType()).hasSize(3)
                .allSatisfy(s -> {
                    assertThat(s.totalAmount()).isEqualByComparingTo("0.00");
                    assertThat(s.invoiceCount()).isZero();
                });
        assertThat(result.grandTotal()).isEqualByComparingTo("0.00");
        assertThat(result.totalInvoices()).isZero();
    }
}
