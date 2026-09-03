package com.project_kata.global_invoices_kata_mngr.infrastructure.persistence;

import com.project_kata.global_invoices_kata_mngr.domain.model.Invoice;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class DashboardRepository {

    private final MongoTemplate mongoTemplate;

    public List<InvoiceTypeAggregate> totalsByType() {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.group("type")
                        .sum("totals.total").as("totalAmount")
                        .count().as("invoiceCount"));

        return mongoTemplate.aggregate(aggregation, Invoice.class, InvoiceTypeAggregate.class)
                .getMappedResults();
    }
}
