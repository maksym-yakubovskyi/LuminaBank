package com.lumina_bank.aiassistantservice.domain.dto.client.account;

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
