package com.lumina_bank.accountservice.application.mapper;

import com.lumina_bank.accountservice.api.response.LoanInstallmentResponse;
import com.lumina_bank.accountservice.api.response.LoanResponse;
import com.lumina_bank.accountservice.domain.enums.InstallmentStatus;
import com.lumina_bank.accountservice.domain.model.Loan;
import com.lumina_bank.accountservice.domain.model.LoanInstallment;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@Component
public class LoanMapper {

    public LoanResponse toResponse(Loan loan) {
        if (loan == null) return null;

        List<LoanInstallment> installments = loan.getInstallments();

        int total = installments.size();
        int paid = (int) installments.stream()
                .filter(i -> i.getStatus() == InstallmentStatus.PAID)
                .count();

        int overdue = (int) installments.stream()
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
                installments.stream()
                        .map(this::toInstallmentResponse)
                        .sorted(Comparator.comparing(LoanInstallmentResponse::installmentNumber))
                        .toList()
        );
    }

    public LoanInstallmentResponse toInstallmentResponse(LoanInstallment inst) {

        BigDecimal paidAmount = inst.getPaidAmount() == null
                ? BigDecimal.ZERO
                : inst.getPaidAmount();

        BigDecimal remaining =
                inst.getTotalAmount().subtract(paidAmount);

        return new LoanInstallmentResponse(
                inst.getId(),
                inst.getInstallmentNumber(),
                inst.getDueDate(),
                inst.getPrincipalPart(),
                inst.getInterestPart(),
                inst.getTotalAmount(),
                paidAmount,
                inst.getPenaltyAmount(),
                remaining,
                inst.getPaidAt(),
                inst.getStatus()
        );
    }

    public List<LoanResponse> toResponseList(List<Loan> loans) {
        return loans.stream()
                .map(this::toResponse)
                .toList();
    }
}