package com.lumina_bank.transactionservice.dto;

import com.lumina_bank.common.enums.payment.Currency;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record TransactionCreateDto(

        @NotNull(message = "Sender card number is required")
        @Positive(message = "Sender card number must be positive")
        String fromCardNumber,

        @NotNull(message = "Receiver card number is required")
        @Positive(message = "Receiver card number must be positive")
        String toCardNumber,

        @NotNull(message = "Transaction amount is required")
        @DecimalMin(value = "0.01", message = "Transaction amount must be greater than 0.00")
        @Digits(integer = 12, fraction = 2, message = "Amount must have up to 12 digits and 2 decimals")
        BigDecimal amount,

        @Size(max = 255, message = "Description must be at most 255 characters long")
        String description,

        Currency fromCurrency,
        Currency toCurrency,
        BigDecimal convertedAmount,
        BigDecimal exchangeRate,
        Long userId,
        Long toAccountOwnerId,
        String category
) {
}