package com.lumina_bank.paymentservice.api.response;

import com.lumina_bank.paymentservice.domain.enums.PaymentStatus;
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
        PaymentStatus status,
        LocalDateTime createdAt
) {}