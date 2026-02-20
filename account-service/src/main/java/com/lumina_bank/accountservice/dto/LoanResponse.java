package com.lumina_bank.accountservice.dto;

import com.lumina_bank.accountservice.enums.InstallmentStatus;
import com.lumina_bank.accountservice.enums.LoanStatus;
import com.lumina_bank.accountservice.model.Loan;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
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

        LoanStatus status,
        Integer riskScore,

        LocalDateTime approvedAt,
        LocalDateTime closedAt,
        LocalDateTime createdAt,

        Integer totalInstallments,
        Integer paidInstallments,
        Integer overdueInstallments,

        List<LoanInstallmentResponse> installments
) {
    public static LoanResponse fromEntity(Loan loan) {

        int total = loan.getInstallments().size();

        int paid = (int) loan.getInstallments().stream()
                .filter(i -> i.getStatus() == InstallmentStatus.PAID)
                .count();

        int overdue = (int) loan.getInstallments().stream()
                .filter(i -> i.getStatus() == InstallmentStatus.OVERDUE)
                .count();

        return new LoanResponse(
                loan.getId(),
                loan.getCreditAccount().getId(),

                loan.getPrincipalAmount(),
                loan.getInterestRate(),
                loan.getTermMonths(),
                loan.getMonthlyPayment(),

                loan.getRemainingPrincipal(),
                loan.getTotalInterestAmount(),
                loan.getTotalPayableAmount(),

                loan.getStatus(),
                loan.getRiskScore(),

                loan.getApprovedAt(),
                loan.getClosedAt(),
                loan.getCreatedAt(),

                total,
                paid,
                overdue,

                loan.getInstallments().stream()
                        .map(LoanInstallmentResponse::fromEntity)
                        .sorted(Comparator.comparing(LoanInstallmentResponse::installmentNumber))
                        .toList()
        );
    }
}
