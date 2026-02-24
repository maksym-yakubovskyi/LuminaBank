package com.lumina_bank.accountservice.api.response;

import com.lumina_bank.accountservice.domain.enums.AccountType;
import com.lumina_bank.accountservice.domain.enums.Status;
import com.lumina_bank.common.enums.payment.Currency;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record AccountResponse(
        Long id,
        Long userId,
        BigDecimal balance,
        String iban,
        Currency currency,
        Status status,
        AccountType type,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}