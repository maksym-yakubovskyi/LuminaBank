package com.lumina_bank.paymentservice.dto;

import com.lumina_bank.paymentservice.model.PaymentTemplate;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record PaymentTemplateResponse(
        Long id,
        Long userId,
        String name,
        String description,
        Long fromAccountId,
        Long toAccountId,
        BigDecimal amount,
        Boolean isRecurring,
        String recurrenceCron
) {
public static PaymentTemplateResponse fromEntity(PaymentTemplate paymentTemplate) {
    return PaymentTemplateResponse.builder()
            .id(paymentTemplate.getId())
            .userId(paymentTemplate.getUserId())
            .name(paymentTemplate.getName())
            .description(paymentTemplate.getDescription())
            .fromAccountId(paymentTemplate.getFromAccountId())
            .toAccountId(paymentTemplate.getToAccountId())
            .amount(paymentTemplate.getAmount())
            .isRecurring(paymentTemplate.getIsRecurring())
            .recurrenceCron(paymentTemplate.getRecurrenceCron())
            .build();
}
}
