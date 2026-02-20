package com.lumina_bank.accountservice.dto;

import com.lumina_bank.accountservice.enums.InstallmentStatus;
import com.lumina_bank.accountservice.model.LoanInstallment;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LoanInstallmentResponse(
        Long id,
        Integer installmentNumber,
        LocalDate dueDate,

        BigDecimal principalPart,
        BigDecimal interestPart,
        BigDecimal totalAmount,

        BigDecimal paidAmount,
        BigDecimal penaltyAmount,
        BigDecimal remainingAmount,

        LocalDate paidAt,
        InstallmentStatus status
) {
    public static LoanInstallmentResponse fromEntity(LoanInstallment inst) {

        BigDecimal remaining =
                inst.getTotalAmount()
                        .subtract(inst.getPaidAmount() == null
                                ? BigDecimal.ZERO
                                : inst.getPaidAmount());

        return new LoanInstallmentResponse(
                inst.getId(),
                inst.getInstallmentNumber(),
                inst.getDueDate(),
                inst.getPrincipalPart(),
                inst.getInterestPart(),
                inst.getTotalAmount(),
                inst.getPaidAmount(),
                inst.getPenaltyAmount(),
                remaining,
                inst.getPaidAt(),
                inst.getStatus()
        );
    }
}
