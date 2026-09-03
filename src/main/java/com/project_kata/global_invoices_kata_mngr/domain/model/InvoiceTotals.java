package com.project_kata.global_invoices_kata_mngr.domain.model;

import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.math.BigDecimal;

public record InvoiceTotals(
        @Field(targetType = FieldType.DECIMAL128) BigDecimal subtotal,
        @Field(targetType = FieldType.DECIMAL128) BigDecimal iva,
        @Field(targetType = FieldType.DECIMAL128) BigDecimal retencion,
        @Field(targetType = FieldType.DECIMAL128) BigDecimal total
) {
}
