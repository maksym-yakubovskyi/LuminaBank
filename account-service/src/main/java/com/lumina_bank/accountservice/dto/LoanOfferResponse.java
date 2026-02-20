package com.lumina_bank.accountservice.dto;

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
