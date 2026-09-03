package com.project_kata.global_invoices_kata_mngr.domain.service;

import com.project_kata.global_invoices_kata_mngr.domain.dto.DashboardSummary;
import com.project_kata.global_invoices_kata_mngr.domain.dto.InvoiceTypeSummary;
import com.project_kata.global_invoices_kata_mngr.domain.model.InvoiceType;
import com.project_kata.global_invoices_kata_mngr.domain.tax.Money;
import com.project_kata.global_invoices_kata_mngr.infrastructure.persistence.DashboardRepository;
import com.project_kata.global_invoices_kata_mngr.infrastructure.persistence.InvoiceTypeAggregate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements IDashboardService {

    private final DashboardRepository dashboardRepository;

    @Override
    public DashboardSummary getSummary() {
        Map<InvoiceType, InvoiceTypeAggregate> byType = new EnumMap<>(InvoiceType.class);
        for (InvoiceTypeAggregate aggregate : dashboardRepository.totalsByType()) {
            byType.put(InvoiceType.valueOf(aggregate.id()), aggregate);
        }

        List<InvoiceTypeSummary> summaries = Arrays.stream(InvoiceType.values())
                .map(type -> {
                    InvoiceTypeAggregate aggregate = byType.get(type);
                    BigDecimal amount = Money.round(aggregate != null ? aggregate.totalAmount() : BigDecimal.ZERO);
                    long count = aggregate != null ? aggregate.invoiceCount() : 0L;
                    return new InvoiceTypeSummary(type, amount, count);
                })
                .toList();

        BigDecimal grandTotal = Money.round(summaries.stream()
                .map(InvoiceTypeSummary::totalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        long totalInvoices = summaries.stream().mapToLong(InvoiceTypeSummary::invoiceCount).sum();

        return new DashboardSummary(summaries, grandTotal, totalInvoices);
    }
}
