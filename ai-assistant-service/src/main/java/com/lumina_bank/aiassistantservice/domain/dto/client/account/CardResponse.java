package com.lumina_bank.aiassistantservice.domain.dto.client.account;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;

public record CardResponse(
        Long id,
        String cardNumber,
        YearMonth expirationDate,
        String cvv,
        String cardType,
        String cardNetwork,
        String accountType,
        String status,
        BigDecimal limit,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Long accountId
) {}
