package com.lumina_bank.paymentservice.api.response;

import com.lumina_bank.paymentservice.domain.enums.PaymentTemplateType;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record PaymentTemplateResponse(
        Long id,
        Long userId,
        PaymentTemplateType type,
        String name,
        String description,
        String fromCardNumber,
        String toCardNumber,
        BigDecimal amount,
        Boolean isRecurring,
        String nextExecutionTime
) {}