package com.lumina_bank.aiassistantservice.infrastructure.external.account.dto;

import com.lumina_bank.aiassistantservice.infrastructure.external.account.enums.CardNetwork;
import com.lumina_bank.aiassistantservice.infrastructure.external.account.enums.CardType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;

public record CardResponse(
        Long id,
        String cardNumber,
        YearMonth expirationDate,
        String cvv,
        CardType cardType,
        CardNetwork cardNetwork,
        String status,
        BigDecimal limit,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Long accountId
) {}
