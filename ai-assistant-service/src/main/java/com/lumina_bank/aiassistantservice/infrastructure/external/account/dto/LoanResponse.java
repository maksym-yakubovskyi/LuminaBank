package com.lumina_bank.aiassistantservice.infrastructure.external.account.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record LoanResponse(
        Long id,
        Long creditAccountId,

        BigDecimal principalAmount,
        BigDecimal interestRate,
        Integer termMonths,
        BigDecimal monthlyPayment,

        BigDecimal remainingPrincipal,
        BigDecimal totalInterestAmount,
        BigDecimal totalPayableAmount,

        String status,
        Integer riskScore,

        LocalDateTime approvedAt,
        LocalDateTime closedAt,
        LocalDateTime createdAt,

        Integer totalInstallments,
        Integer paidInstallments,
        Integer overdueInstallments,

        List<LoanInstallmentResponse> installments
) {}
