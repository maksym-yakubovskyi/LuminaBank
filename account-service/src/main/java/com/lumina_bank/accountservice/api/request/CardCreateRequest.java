package com.lumina_bank.accountservice.api.request;

import com.lumina_bank.accountservice.domain.enums.CardNetwork;
import com.lumina_bank.accountservice.domain.enums.CardType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record CardCreateRequest(
        @NotNull CardType cardType,
        @NotNull CardNetwork cardNetwork,
        @PositiveOrZero BigDecimal limit
) {}