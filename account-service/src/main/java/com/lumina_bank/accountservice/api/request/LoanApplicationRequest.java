package com.lumina_bank.accountservice.api.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record LoanApplicationRequest(
        @NotNull Long creditAccountId,
        @NotNull @Positive BigDecimal requestedAmount,
        @NotNull @Min(1) Integer requestedTermMonths
) {}