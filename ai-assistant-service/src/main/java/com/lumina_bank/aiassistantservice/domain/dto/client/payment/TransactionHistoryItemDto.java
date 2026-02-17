package com.lumina_bank.aiassistantservice.domain.dto.client.payment;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionHistoryItemDto(

        Long paymentId,

        // для OUTGOING / INCOMING
        BigDecimal amount,
        String currency,

        // для INTERNAL (user-level history)
        BigDecimal outgoingAmount,
        String outgoingCurrency,
        BigDecimal incomingAmount,
        String incomingCurrency,

        LocalDate date,
        String direction,
        String status,
        String description
) {
}
