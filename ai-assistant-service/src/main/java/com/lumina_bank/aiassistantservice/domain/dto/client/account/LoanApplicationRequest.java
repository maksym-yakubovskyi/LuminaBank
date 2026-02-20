package com.lumina_bank.aiassistantservice.domain.dto.client.account;

import java.math.BigDecimal;

public record LoanApplicationRequest(
        Long creditAccountId,
        BigDecimal requestedAmount,
        Integer requestedTermMonths
) {}
