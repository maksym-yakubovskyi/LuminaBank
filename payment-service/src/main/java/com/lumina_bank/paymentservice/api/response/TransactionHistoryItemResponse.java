package com.lumina_bank.paymentservice.api.response;

import com.lumina_bank.common.enums.payment.Currency;
import com.lumina_bank.paymentservice.domain.enums.PaymentDirection;
import com.lumina_bank.paymentservice.domain.enums.PaymentStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record TransactionHistoryItemResponse(

        Long paymentId,

        // для OUTGOING / INCOMING
        BigDecimal amount,
        Currency currency,

        // для INTERNAL (user-level history)
        BigDecimal outgoingAmount,
        Currency outgoingCurrency,
        BigDecimal incomingAmount,
        Currency incomingCurrency,

        LocalDate date,
        PaymentDirection direction,
        PaymentStatus status,
        String description
) {}