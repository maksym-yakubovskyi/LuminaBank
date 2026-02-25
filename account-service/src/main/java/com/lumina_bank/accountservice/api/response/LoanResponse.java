package com.lumina_bank.accountservice.api.response;

import com.lumina_bank.accountservice.domain.enums.LoanStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
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

        LoanStatus status,
        Integer riskScore,

        LocalDateTime approvedAt,
        LocalDateTime closedAt,
        LocalDateTime createdAt,

        Integer totalInstallments,
        Integer paidInstallments,
        Integer overdueInstallments,

        List<LoanInstallmentResponse> installments
) {}
