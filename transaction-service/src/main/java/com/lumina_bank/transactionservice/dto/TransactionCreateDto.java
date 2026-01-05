package com.lumina_bank.transactionservice.dto;

import com.lumina_bank.common.enums.payment.Currency;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record TransactionCreateDto(

        @NotNull(message = "Sender account ID is required")
        @Positive(message = "Sender account ID must be positive")
        Long fromAccountId,

        @NotNull(message = "Receiver account ID is required")
        @Positive(message = "Receiver account ID must be positive")
        Long toAccountId,

        @NotNull(message = "Transaction amount is required")
        @DecimalMin(value = "0.01", message = "Transaction amount must be greater than 0.00")
        @Digits(integer = 12, fraction = 2, message = "Amount must have up to 12 digits and 2 decimals")
        BigDecimal amount,

        @Size(max = 255, message = "Description must be at most 255 characters long")
        String description,

        Currency fromCurrency,
        Currency toCurrency,
        BigDecimal convertedAmount,
        BigDecimal exchangeRate
) {
}
