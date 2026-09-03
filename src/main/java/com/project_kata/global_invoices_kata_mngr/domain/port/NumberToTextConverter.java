package com.project_kata.global_invoices_kata_mngr.domain.port;

import java.math.BigDecimal;
import java.util.Optional;

public interface NumberToTextConverter {
    Optional<String> toText(BigDecimal amount);
}
