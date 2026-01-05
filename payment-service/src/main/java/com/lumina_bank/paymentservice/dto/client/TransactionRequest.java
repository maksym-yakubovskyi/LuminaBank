package com.lumina_bank.paymentservice.dto.client;

import com.lumina_bank.common.enums.payment.Currency;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record TransactionRequest (
        Long fromAccountId,
        Long toAccountId,
        Currency fromCurrency,
        Currency toCurrency,
        BigDecimal amount,
        BigDecimal convertedAmount,
        BigDecimal exchangeRate,
        String description
) {
}
