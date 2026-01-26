package com.lumina_bank.accountservice.dto;

import com.lumina_bank.accountservice.enums.CardNetwork;
import com.lumina_bank.accountservice.enums.CardType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record CardCreateDto(
        @NotNull(message = "Card type is required")
        CardType cardType,
        @NotNull(message = "Card network is required")
        CardNetwork cardNetwork,
        @PositiveOrZero(message = "Limit must be positive or zero")
        BigDecimal limit
) {
}