package com.project_kata.global_invoices_kata_mngr.domain.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * El campo {@code codigoAduanero} es obligatorio para facturas de EXPORTACION y no debe
 * enviarse para el resto de tipos (RF-02).
 */
@Documented
@Constraint(validatedBy = ConsistentCustomsCodeValidator.class)
@Target(TYPE)
@Retention(RUNTIME)
public @interface ConsistentCustomsCode {

    String message() default "El codigo aduanero no es consistente con el tipo de factura";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
