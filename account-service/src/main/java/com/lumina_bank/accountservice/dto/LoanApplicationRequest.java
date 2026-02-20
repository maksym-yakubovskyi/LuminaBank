package com.lumina_bank.accountservice.dto;

import java.math.BigDecimal;

public record LoanApplicationRequest(
        Long creditAccountId,
        BigDecimal requestedAmount,
        Integer requestedTermMonths
) {}