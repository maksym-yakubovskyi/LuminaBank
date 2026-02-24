package com.lumina_bank.aiassistantservice.infrastructure.external.account.dto;

import java.math.BigDecimal;

public record LoanApplicationRequest(
        Long creditAccountId,
        BigDecimal requestedAmount,
        Integer requestedTermMonths
) {}
