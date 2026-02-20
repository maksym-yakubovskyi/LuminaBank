package com.lumina_bank.aiassistantservice.domain.dto.client.account;


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
        String  status
) {}
