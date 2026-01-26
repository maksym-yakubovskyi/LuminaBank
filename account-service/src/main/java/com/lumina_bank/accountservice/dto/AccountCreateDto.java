package com.lumina_bank.accountservice.dto;

import com.lumina_bank.accountservice.enums.AccountType;
import com.lumina_bank.common.enums.payment.Currency;
import jakarta.validation.constraints.NotNull;

public record AccountCreateDto(
        @NotNull(message = "Currency is required")
        Currency currency,
        @NotNull(message = "Account type is required")
        AccountType type
) {
}