package com.project_kata.global_invoices_kata_mngr.domain.validation;

import com.project_kata.global_invoices_kata_mngr.domain.dto.CreateInvoiceRequest;
import com.project_kata.global_invoices_kata_mngr.domain.model.InvoiceType;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ConsistentCustomsCodeValidatorTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        factory.close();
    }

    private Set<ConstraintViolation<CreateInvoiceRequest>> validate(InvoiceType type, String code) {
        return validator.validate(
                new CreateInvoiceRequest(type, "Servicio de prueba", new BigDecimal("100"), code));
    }

    @Test
    void exportacionWithCustomsCodeIsValid() {
        assertThat(validate(InvoiceType.EXPORTACION, "COL-12345")).isEmpty();
    }

    @Test
    void exportacionWithoutCustomsCodeIsRejectedOnThatField() {
        var violations = validate(InvoiceType.EXPORTACION, "   ");

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath()).hasToString("codigoAduanero");
    }

    @Test
    void nacionalWithCustomsCodeIsRejected() {
        var violations = validate(InvoiceType.NACIONAL, "COL-12345");

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath()).hasToString("codigoAduanero");
    }

    @Test
    void nacionalWithoutCustomsCodeIsValid() {
        assertThat(validate(InvoiceType.NACIONAL, null)).isEmpty();
    }

    @Test
    void nullTypeYieldsNoCrossFieldViolation() {
        var violations = validator.validate(
                new CreateInvoiceRequest(null, "Servicio de prueba", new BigDecimal("100"), "COL-1"));

        assertThat(violations).extracting(v -> v.getPropertyPath().toString())
                .containsExactly("type");
    }
}
