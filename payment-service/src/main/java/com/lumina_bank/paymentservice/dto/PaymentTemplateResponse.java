package com.lumina_bank.paymentservice.dto;

import com.lumina_bank.paymentservice.model.PaymentTemplate;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record PaymentTemplateResponse(
        Long id,
        Long userId,
        String type,
        String name,
        String description,
        String fromCardNumber,
        String toCardNumber,
        BigDecimal amount,
        Boolean isRecurring,
        String nextExecutionTime
) {
    public static PaymentTemplateResponse fromEntity(PaymentTemplate paymentTemplate) {
        return PaymentTemplateResponse.builder()
                .id(paymentTemplate.getId())
                .userId(paymentTemplate.getUserId())
                .type(paymentTemplate.getType().name())
                .name(paymentTemplate.getName())
                .description(paymentTemplate.getDescription())
                .fromCardNumber(paymentTemplate.getFromCardNumber())
                .toCardNumber(paymentTemplate.getToCardNumber())
                .amount(paymentTemplate.getAmount())
                .isRecurring(paymentTemplate.getIsRecurring())
                .nextExecutionTime(
                        paymentTemplate.getNextExecutionTime() != null
                                ? paymentTemplate.getNextExecutionTime().toString()
                                : null
                )
                .build();
    }
}