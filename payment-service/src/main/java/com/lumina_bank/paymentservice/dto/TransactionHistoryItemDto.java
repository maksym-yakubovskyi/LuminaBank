package com.lumina_bank.paymentservice.dto;

import com.lumina_bank.common.enums.payment.PaymentDirection;
import com.lumina_bank.paymentservice.model.Payment;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
@Builder
public record TransactionHistoryItemDto(

        Long paymentId,

        // для OUTGOING / INCOMING
        BigDecimal amount,
        String currency,

        // для INTERNAL (user-level history)
        BigDecimal outgoingAmount,
        String outgoingCurrency,
        BigDecimal incomingAmount,
        String incomingCurrency,

        LocalDate date,
        String direction,
        String status,
        String description
) {

    public static TransactionHistoryItemDto toHistoryItem(
            Payment p,
            Long userId,
            Long accountId
    ) {

        boolean isSender = p.getUserId().equals(userId);
        boolean isReceiver = p.getToAccountOwnerId() != null
                && p.getToAccountOwnerId().equals(userId);

        boolean isInternal = isSender && isReceiver;

        String direction;

        if (accountId != null) {

            if (p.getFromAccountId() != null
                    && p.getFromAccountId().equals(accountId)) {
                direction = PaymentDirection.OUTGOING.name();

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

            direction = PaymentDirection.INCOMING.name();

            return TransactionHistoryItemDto.builder()
                    .paymentId(p.getId())
                    .amount(
                            p.getConvertedAmount() != null
                                    ? p.getConvertedAmount()
                                    : p.getAmount()
                    )
                    .currency(
                            p.getToCurrency() != null
                                    ? p.getToCurrency().name()
                                    : p.getFromCurrency().name()
                    )
                    .date(p.getCreatedAt().toLocalDate())
                    .direction(direction)
                    .status(p.getPaymentStatus().name())
                    .description(p.getDescription())
                    .build();
        }

        if (isInternal) {

            return TransactionHistoryItemDto.builder()
                    .paymentId(p.getId())

                    // для INTERNAL amount/currency не використовуємо
                    .outgoingAmount(p.getAmount())
                    .outgoingCurrency(p.getFromCurrency().name())
                    .incomingAmount(
                            p.getConvertedAmount() != null
                                    ? p.getConvertedAmount()
                                    : p.getAmount()
                    )
                    .incomingCurrency(
                            p.getToCurrency() != null
                                    ? p.getToCurrency().name()
                                    : p.getFromCurrency().name()
                    )

                    .date(p.getCreatedAt().toLocalDate())
                    .direction(PaymentDirection.INTERNAL.name())
                    .status(p.getPaymentStatus().name())
                    .description(p.getDescription())
                    .build();
        }

        if (isSender) {

            return TransactionHistoryItemDto.builder()
                    .paymentId(p.getId())
                    .amount(p.getAmount())
                    .currency(p.getFromCurrency().name())
                    .date(p.getCreatedAt().toLocalDate())
                    .direction(PaymentDirection.OUTGOING.name())
                    .status(p.getPaymentStatus().name())
                    .description(p.getDescription())
                    .build();
        }

        return TransactionHistoryItemDto.builder()
                .paymentId(p.getId())
                .amount(
                        p.getConvertedAmount() != null
                                ? p.getConvertedAmount()
                                : p.getAmount()
                )
                .currency(
                        p.getToCurrency() != null
                                ? p.getToCurrency().name()
                                : p.getFromCurrency().name()
                )
                .date(p.getCreatedAt().toLocalDate())
                .direction(PaymentDirection.INCOMING.name())
                .status(p.getPaymentStatus().name())
                .description(p.getDescription())
                .build();
    }
}