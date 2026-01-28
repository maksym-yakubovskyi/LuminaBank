package com.lumina_bank.paymentservice.dto;

import com.lumina_bank.common.enums.payment.PaymentDirection;
import com.lumina_bank.paymentservice.model.Payment;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record TransactionHistoryItemDto(
        Long paymentId,
        BigDecimal amount,
        String currency,
        LocalDate date,
        String direction,
        String status,
        String description
) {

    static public TransactionHistoryItemDto toHistoryItem(Payment p, Long accountId){
        String direction = p.getFromAccountId().equals(accountId) ?
                PaymentDirection.OUTGOING.name() : PaymentDirection.INCOMING.name();

        return TransactionHistoryItemDto.builder()
                .paymentId(p.getId())
                .amount(p.getAmount())
                .currency(p.getFromCurrency().name())
                .date(p.getCreatedAt().toLocalDate())
                .direction(direction)
                .status(p.getPaymentStatus().name())
                .description(p.getDescription())
                .build();
    }
}