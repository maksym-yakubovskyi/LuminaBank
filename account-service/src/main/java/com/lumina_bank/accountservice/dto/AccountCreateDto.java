package com.lumina_bank.accountservice.dto;

import com.lumina_bank.accountservice.enums.AccountType;
import com.lumina_bank.accountservice.enums.Currency;
import jakarta.validation.constraints.NotNull;


public record AccountCreateDto(
        @NotNull(message = "User ID is required")
        Long userId,
        @NotNull(message = "Currency is required")
        Currency currency,
        @NotNull(message = "Account type is required")
        AccountType type
) {
}
