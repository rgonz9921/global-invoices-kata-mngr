package com.project_kata.global_invoices_kata_mngr.domain.validation;

import com.project_kata.global_invoices_kata_mngr.domain.dto.CreateInvoiceRequest;
import com.project_kata.global_invoices_kata_mngr.domain.model.InvoiceType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ConsistentCustomsCodeValidator
        implements ConstraintValidator<ConsistentCustomsCode, CreateInvoiceRequest> {

    private static final String FIELD = "customsCode";

    @Override
    public boolean isValid(CreateInvoiceRequest request, ConstraintValidatorContext context) {
        if (request == null || request.type() == null) {
            return true;
        }

        boolean present = request.customsCode() != null && !request.customsCode().isBlank();
        boolean isExport = request.type() == InvoiceType.EXPORTACION;

        if (isExport && !present) {
            return reject(context, "El codigo aduanero es obligatorio para facturas de exportacion");
        }
        if (!isExport && present) {
            return reject(context, "El codigo aduanero solo aplica a facturas de exportacion");
        }
        return true;
    }

    private boolean reject(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addPropertyNode(FIELD)
                .addConstraintViolation();
        return false;
    }
}
