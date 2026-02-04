package com.lumina_bank.accountservice.dto;

import com.lumina_bank.accountservice.model.Account;
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
        String status,
        String type,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static AccountResponse fromEntity(Account account) {
        return AccountResponse.builder()
                .id(account.getId())
                .userId(account.getUserId())
                .balance(account.getBalance())
                .iban(account.getIban())
                .currency(account.getCurrency())
                .status(account.getStatus().name())
                .type(account.getType().name())
                .createdAt(account.getCreatedAt())
                .updatedAt(account.getUpdatedAt())
                .build();
    }
}