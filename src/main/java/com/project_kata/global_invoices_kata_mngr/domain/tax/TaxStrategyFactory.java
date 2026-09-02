package com.project_kata.global_invoices_kata_mngr.domain.tax;

import com.project_kata.global_invoices_kata_mngr.domain.exception.UnsupportedInvoiceTypeException;
import com.project_kata.global_invoices_kata_mngr.domain.model.InvoiceType;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class TaxStrategyFactory {

    private final Map<InvoiceType, TaxStrategy> strategiesByType;

    public TaxStrategyFactory(List<TaxStrategy> strategies) {
        this.strategiesByType = strategies.stream()
                .collect(Collectors.toMap(TaxStrategy::getSupportedType, Function.identity(),
                        (a, b) -> {
                            throw new IllegalStateException(
                                    "Dos TaxStrategy declaran el mismo tipo: " + a.getSupportedType());
                        },
                        () -> new EnumMap<>(InvoiceType.class)));
    }

    @PostConstruct
    void verifyAllTypesCovered() {
        for (InvoiceType type : InvoiceType.values()) {
            if (!strategiesByType.containsKey(type)) {
                throw new IllegalStateException(
                        "Falta una TaxStrategy @Component para el tipo de factura: " + type);
            }
        }
    }

    public TaxStrategy getStrategy(InvoiceType type) {
        TaxStrategy strategy = strategiesByType.get(type);
        if (strategy == null) {
            throw new UnsupportedInvoiceTypeException(type);
        }
        return strategy;
    }

    public Set<InvoiceType> getSupportedTypes() {
        return Set.copyOf(strategiesByType.keySet());
    }
}
