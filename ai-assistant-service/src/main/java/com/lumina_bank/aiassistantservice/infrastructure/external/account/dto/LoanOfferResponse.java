package com.lumina_bank.aiassistantservice.infrastructure.external.account.dto;

import java.math.BigDecimal;

public record LoanOfferResponse(
        BigDecimal approvedAmount,
        BigDecimal interestRate,
        Integer termMonths,
        BigDecimal monthlyPayment,
        BigDecimal totalPayable,
        Integer riskScore,
        boolean approved
) {}
