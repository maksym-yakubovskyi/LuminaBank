package com.lumina_bank.paymentservice.api.request;

import com.lumina_bank.paymentservice.domain.enums.PaymentTemplateType;
import com.lumina_bank.paymentservice.domain.enums.RecurrenceType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.DayOfWeek;

public record PaymentTemplateRequest(
        @NotBlank String name,
        @NotNull String fromCardNumber,
        @NotNull PaymentTemplateType type,

        // TRANSFER
        String toCardNumber,

        // SERVICE
        Long providerId,
        String payerReference,
        String category,

        @NotNull @DecimalMin(value = "0.01") BigDecimal amount,
        @Size(max = 255) String description,
        @NotNull RecurrenceType recurrenceType,

        Integer hour,
        Integer minute,
        DayOfWeek dayOfWeek,
        Integer dayOfMonth
) {
}