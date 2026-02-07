package com.lumina_bank.paymentservice.dto;

import com.lumina_bank.paymentservice.model.Payment;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record PaymentResponse(
        Long id,
        String fromCardNumber,
        String toCardNumber,
        BigDecimal amount,
        String description,
        String status,
        LocalDateTime createdAt
) {
    public static PaymentResponse fromEntity(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .fromCardNumber(payment.getFromCardNumber())
                .toCardNumber(payment.getToCardNumber())
                .amount(payment.getAmount())
                .description(payment.getDescription())
                .status(payment.getPaymentStatus().name())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}