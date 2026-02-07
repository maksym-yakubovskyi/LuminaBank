package com.lumina_bank.paymentservice.dto;

import com.lumina_bank.paymentservice.enums.PaymentTemplateType;
import com.lumina_bank.paymentservice.enums.RecurrenceType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.DayOfWeek;

public record PaymentTemplateRequest(
        @NotBlank(message = "Name is required")
        String name,            // Назва шаблону, наприклад "Оплата комуналки"

        @NotNull(message = "From account ID is required")
        String fromCardNumber,     // Звідки платимо

        @NotNull
        PaymentTemplateType type,

        // TRANSFER
        String toCardNumber,

        // SERVICE
        Long providerId,
        String payerReference,
        String category,

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
        BigDecimal amount,      // Типова сума

        @Size(max = 255, message = "Description must be less than 255 characters")
        String description,      // Призначення платежу

        @NotNull RecurrenceType recurrenceType,
        Integer hour,
        Integer minute,
        DayOfWeek dayOfWeek,
        Integer dayOfMonth
) {
}