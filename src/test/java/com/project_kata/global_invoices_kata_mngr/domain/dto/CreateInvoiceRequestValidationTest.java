package com.project_kata.global_invoices_kata_mngr.domain.dto;

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

class CreateInvoiceRequestValidationTest {

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

    private Set<ConstraintViolation<CreateInvoiceRequest>> validate(String concepto) {
        return validator.validate(
                new CreateInvoiceRequest(InvoiceType.NACIONAL, concepto, new BigDecimal("100"), null));
    }

    @Test
    void acceptsANonBlankConcepto() {
        assertThat(validate("Consultoria mensual")).isEmpty();
    }

    @Test
    void rejectsBlankConcepto() {
        assertThat(validate("   "))
                .extracting(v -> v.getPropertyPath().toString())
                .containsExactly("concepto");
    }

    @Test
    void rejectsNullConcepto() {
        assertThat(validate(null))
                .extracting(v -> v.getPropertyPath().toString())
                .containsExactly("concepto");
    }

    @Test
    void rejectsConceptoOver200Chars() {
        assertThat(validate("x".repeat(201)))
                .extracting(v -> v.getPropertyPath().toString())
                .containsExactly("concepto");
    }
}
