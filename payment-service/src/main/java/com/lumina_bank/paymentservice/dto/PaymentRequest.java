package com.lumina_bank.paymentservice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record PaymentRequest(

        @NotBlank(message = "From card number is required")
        @Size(min = 16, max = 16, message = "From card number must be 16 digits")
        String fromCardNumber,

        @NotBlank(message = "To card number is required")
        @Size(min = 16, max = 16, message = "To card number must be 16 digits")
        String toCardNumber,

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
        BigDecimal amount,

        @Size(max = 255, message = "Description must be less than 255 characters")
        String description
) {
}