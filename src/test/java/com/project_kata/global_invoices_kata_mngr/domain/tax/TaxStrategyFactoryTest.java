package com.project_kata.global_invoices_kata_mngr.domain.tax;

import com.project_kata.global_invoices_kata_mngr.domain.exception.UnsupportedInvoiceTypeException;
import com.project_kata.global_invoices_kata_mngr.domain.model.InvoiceType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TaxStrategyFactoryTest {

    private final TaxStrategyFactory factory = new TaxStrategyFactory(List.of(
            new NationalTaxStrategy(), new ExportTaxStrategy(), new GovernmentTaxStrategy()));

    @ParameterizedTest
    @EnumSource(InvoiceType.class)
    void resolvesEveryTypeToItsOwnStrategy(InvoiceType type) {
        assertThat(factory.getStrategy(type).getSupportedType()).isEqualTo(type);
    }

    @Test
    void reportsAllSupportedTypes() {
        assertThat(factory.getSupportedTypes())
                .containsExactlyInAnyOrder(
                        InvoiceType.NACIONAL, InvoiceType.EXPORTACION, InvoiceType.GUBERNAMENTAL);
    }

    @Test
    void failsFastWhenTwoStrategiesShareType() {
        assertThatThrownBy(() -> new TaxStrategyFactory(
                List.of(new NationalTaxStrategy(), new NationalTaxStrategy())))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void throwsUnsupportedWhenNoStrategyForType() {
        TaxStrategyFactory incomplete = new TaxStrategyFactory(List.of(new NationalTaxStrategy()));

        assertThatThrownBy(() -> incomplete.getStrategy(InvoiceType.EXPORTACION))
                .isInstanceOf(UnsupportedInvoiceTypeException.class);
    }

    @Test
    void postConstructCheckFailsWhenATypeHasNoStrategy() {
        TaxStrategyFactory incomplete = new TaxStrategyFactory(List.of(new NationalTaxStrategy()));

        assertThatThrownBy(incomplete::verifyAllTypesCovered)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("EXPORTACION");
    }

    @Test
    void postConstructCheckPassesWithAllStrategies() {
        factory.verifyAllTypesCovered();
    }
}
