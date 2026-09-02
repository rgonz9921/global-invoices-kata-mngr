package com.project_kata.global_invoices_kata_mngr.domain.tax;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class Money {

    public static final int SCALE = 2;
    public static final RoundingMode ROUNDING = RoundingMode.HALF_UP;

    private Money() {
    }

    public static BigDecimal round(BigDecimal value) {
        return value.setScale(SCALE, ROUNDING);
    }

    public static BigDecimal percentageOf(BigDecimal base, BigDecimal rate) {
        return round(base.multiply(rate));
    }

    public static BigDecimal requireNonNegative(BigDecimal value) {
        if (value == null) {
            throw new IllegalArgumentException("El subtotal es obligatorio");
        }
        if (value.signum() < 0) {
            throw new IllegalArgumentException("El subtotal no puede ser negativo");
        }
        return value;
    }
}
