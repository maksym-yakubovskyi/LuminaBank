package com.lumina_bank.transactionservice.dto.client;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record AccountOperationDto(
        BigDecimal amount,
        String cardNumber
) {
}