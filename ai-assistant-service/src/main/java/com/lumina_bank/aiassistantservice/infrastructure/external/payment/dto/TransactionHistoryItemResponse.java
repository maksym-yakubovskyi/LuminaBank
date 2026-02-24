package com.lumina_bank.aiassistantservice.infrastructure.external.payment.dto;

import com.lumina_bank.common.enums.payment.Currency;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionHistoryItemResponse(

        Long paymentId,

        // для OUTGOING / INCOMING
        BigDecimal amount,
        Currency currency,

        // для INTERNAL (user-level history)
        BigDecimal outgoingAmount,
        Currency outgoingCurrency,
        BigDecimal incomingAmount,
        Currency incomingCurrency,

        LocalDate date,
        String direction,
        String status,
        String description
) {
}
