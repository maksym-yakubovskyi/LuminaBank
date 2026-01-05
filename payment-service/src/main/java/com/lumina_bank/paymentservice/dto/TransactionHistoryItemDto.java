package com.lumina_bank.paymentservice.dto;

import com.lumina_bank.common.enums.payment.PaymentDirection;
import com.lumina_bank.paymentservice.model.Payment;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record TransactionHistoryItemDto(
        Long paymentId,
        String type,
        BigDecimal amount,
        String currency,
        LocalDate date,
        String direction,
        String status
) {

    static public TransactionHistoryItemDto toHistoryItem(Payment p, Long accountId){
        String direction = p.getFromAccountId().equals(accountId) ?
                PaymentDirection.OUTGOING.name() : PaymentDirection.INCOMING.name();

        return TransactionHistoryItemDto.builder()
                .paymentId(p.getId())
                .type(p.getPaymentType().name())
                .amount(p.getAmount())
                .currency(p.getFromCurrency().name())
                .date(p.getCreatedAt().toLocalDate())
                .direction(direction)
                .status(p.getPaymentStatus().name())
                .build();
    }
}
