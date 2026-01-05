package com.lumina_bank.transactionservice.dto;

import com.lumina_bank.common.enums.payment.PaymentDirection;
import com.lumina_bank.transactionservice.model.Transaction;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record TransactionResponse(
        Long id,
        Long fromAccountId,
        Long toAccountId,
        BigDecimal amount,
        String status,
        String direction) {

    public static TransactionResponse fromEntity(Transaction t, Long currentAccountId) {
        String direction = t.getFromAccountId().equals(currentAccountId) ?
                PaymentDirection.OUTGOING.name() : PaymentDirection.INCOMING.name();

        return TransactionResponse.builder()
                .id(t.getId())
                .fromAccountId(t.getFromAccountId())
                .toAccountId(t.getToAccountId())
                .amount(t.getAmount())
                .status(t.getTransactionStatus().name())
                .direction(direction)
                .build();
    }
}
