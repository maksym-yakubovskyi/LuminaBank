package com.lumina_bank.accountservice.api.response;

import com.lumina_bank.accountservice.domain.enums.CardNetwork;
import com.lumina_bank.accountservice.domain.enums.CardType;
import com.lumina_bank.accountservice.domain.enums.Status;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;

@Builder
public record CardResponse(
        Long id,
        String cardNumber,
        YearMonth expirationDate,
        String cvv,
        CardType cardType,
        CardNetwork cardNetwork,
        Status status,
        BigDecimal limit,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Long accountId
) {}