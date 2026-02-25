package com.lumina_bank.paymentservice.api.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ServicePaymentRequest(
        @NotBlank @Size(min = 16, max = 16) String fromCardNumber,
        @NotNull Long providerId,
        @NotBlank String category,
        @NotNull @DecimalMin(value = "0.01") BigDecimal amount,
        @NotBlank @Size(max = 64) String payerReference,
        @Size(max = 255) String description
) {}