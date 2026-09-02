package com.project_kata.global_invoices_kata_mngr.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;

@Document(collection = "invoices")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Invoice {

    @Id
    private String id;

    private InvoiceType type;

    /** Descripcion de la linea facturada (ej. "Consultoria mensual"). */
    private String concepto;

    private BigDecimal subtotal;

    /** Solo presente para facturas de EXPORTACION (RF-02). */
    private String codigoAduanero;

    /** Totales calculados por el motor tributario y persistidos. */
    private InvoiceTotals totals;

    @CreatedDate
    private Instant createdAt;

    @CreatedBy
    private String createdBy;
}
