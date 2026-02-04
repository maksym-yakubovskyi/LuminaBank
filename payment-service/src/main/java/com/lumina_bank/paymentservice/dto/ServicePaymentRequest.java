package com.lumina_bank.paymentservice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ServicePaymentRequest(

        @NotBlank(message = "From card number is required")
        @Size(min = 16, max = 16, message = "From card number must be 16 digits")
        String fromCardNumber,

        @NotNull(message = "Provider ID is required")
        Long providerId,

        @NotBlank
        String category,

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
        BigDecimal amount,

        @NotBlank(message = "Payer reference is required")
        @Size(max = 64, message = "Reference must be less than 64 characters")
        String payerReference,

        @Size(max = 255, message = "Description must be less than 255 characters")
        String description
) {}