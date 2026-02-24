package com.lumina_bank.accountservice.api.response;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record LoanOfferResponse(
        BigDecimal approvedAmount,
        BigDecimal interestRate,
        Integer termMonths,
        BigDecimal monthlyPayment,
        BigDecimal totalPayable,
        Integer riskScore,
        boolean approved
) {}
