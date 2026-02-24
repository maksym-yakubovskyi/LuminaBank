package com.lumina_bank.transactionservice.api.request;

import com.lumina_bank.common.enums.payment.Currency;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record TransactionCreateRequest(
        @NotNull @Positive String fromCardNumber,
        @NotNull @Positive String toCardNumber,

        @NotNull @DecimalMin(value = "0.01") @Digits(integer = 12, fraction = 2) BigDecimal amount,

        @Size(max = 255) String description,

        Currency fromCurrency,
        Currency toCurrency,
        BigDecimal convertedAmount,
        BigDecimal exchangeRate,
        Long userId,
        Long toAccountOwnerId,
        String category
) {}