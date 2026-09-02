package com.project_kata.global_invoices_kata_mngr.domain.tax;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MoneyTest {

    @Test
    void roundsHalfUpToTwoDecimals() {
        assertThat(Money.round(new BigDecimal("19.0095"))).isEqualByComparingTo("19.01");
        assertThat(Money.round(new BigDecimal("19.004"))).isEqualByComparingTo("19.00");
        assertThat(Money.round(new BigDecimal("19.005"))).isEqualByComparingTo("19.01");
    }

    @Test
    void roundAlwaysHasScaleTwo() {
        assertThat(Money.round(new BigDecimal("10")).scale()).isEqualTo(2);
    }

    @Test
    void percentageOfRoundsResult() {
        assertThat(Money.percentageOf(new BigDecimal("100.05"), new BigDecimal("0.19")))
                .isEqualByComparingTo("19.01");
        assertThat(Money.percentageOf(new BigDecimal("1000"), new BigDecimal("0.19")))
                .isEqualByComparingTo("190.00");
    }

    @Test
    void requireNonNegativeRejectsNullAndNegative() {
        assertThatThrownBy(() -> Money.requireNonNegative(null))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> Money.requireNonNegative(new BigDecimal("-0.01")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void requireNonNegativeAcceptsZeroAndPositive() {
        assertThat(Money.requireNonNegative(BigDecimal.ZERO)).isEqualByComparingTo("0");
        assertThat(Money.requireNonNegative(new BigDecimal("5"))).isEqualByComparingTo("5");
    }
}
