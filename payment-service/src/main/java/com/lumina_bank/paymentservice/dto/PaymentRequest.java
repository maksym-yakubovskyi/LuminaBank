package com.lumina_bank.paymentservice.dto;

import com.lumina_bank.paymentservice.enums.PaymentType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record PaymentRequest(
        @NotNull(message = "User ID is required")
        Long userId,

        @NotNull(message = "From account ID is required")
        Long fromAccountId,

        @NotNull(message = "To account ID is required")
        Long toAccountId,

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
        BigDecimal amount,

        @Size(max = 255, message = "Description must be less than 255 characters")
        String description,

        @NotNull(message = "Payment type is required")
        PaymentType paymentType,

        Long templateId
) {
}
