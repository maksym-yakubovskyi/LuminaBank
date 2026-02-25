package com.lumina_bank.transactionservice.infrastructure.external.account.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record AccountOperationRequest(
        BigDecimal amount,
        String cardNumber
) {
}