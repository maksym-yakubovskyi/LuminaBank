package com.lumina_bank.accountservice.api.response;

import com.lumina_bank.accountservice.domain.enums.InstallmentStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
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
) {}
