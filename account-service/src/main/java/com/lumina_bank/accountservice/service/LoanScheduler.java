package com.lumina_bank.accountservice.service;

import com.lumina_bank.accountservice.enums.InstallmentStatus;
import com.lumina_bank.accountservice.enums.LoanStatus;
import com.lumina_bank.accountservice.model.Loan;
import com.lumina_bank.accountservice.model.LoanInstallment;
import com.lumina_bank.accountservice.repository.LoanInstallmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoanScheduler {
    private final LoanInstallmentRepository installmentRepository;

    private static final BigDecimal DAILY_PENALTY_RATE =
            new BigDecimal("0.001");

    @Scheduled(cron = "0 0 0 * * ?") // щодня о 00:00
    @Transactional
    public void markInstallmentsAsDue() {

        LocalDate today = LocalDate.now();

        List<LoanInstallment> installments =
                installmentRepository
                        .findAllByStatusAndDueDate(
                                InstallmentStatus.PENDING,
                                today
                        );

        for (LoanInstallment inst : installments) {
            inst.setStatus(InstallmentStatus.DUE);
        }

        log.info("Marked {} installments as DUE", installments.size());
    }

    @Scheduled(cron = "0 0 1 * * ?")
    @Transactional
    public void processOverdue() {

        LocalDate today = LocalDate.now();

        List<LoanInstallment> overdue =
                installmentRepository
                        .findAllByStatusAndDueDateBefore(
                                InstallmentStatus.DUE,
                                today
                        );

        for (LoanInstallment inst : overdue) {

            if (inst.getStatus() == InstallmentStatus.PAID)
                continue;

            inst.setStatus(InstallmentStatus.OVERDUE);

            Loan loan = inst.getLoan();
            loan.setStatus(LoanStatus.OVERDUE);

            // пеня 1% тільки один раз при переході
            if (inst.getPaidAmount()
                    .compareTo(inst.getTotalAmount()) < 0) {

                BigDecimal penalty =
                        inst.getTotalAmount()
                                .multiply(new BigDecimal("0.01"));

                inst.setTotalAmount(
                        inst.getTotalAmount().add(penalty)
                );
            }
        }

        log.info("Processed overdue installments");
    }

    @Scheduled(cron = "0 30 1 * * ?") // 01:30 щодня
    @Transactional
    public void accrueDailyPenalty() {
        List<LoanInstallment> overdueInstallments =
                installmentRepository.findAllByStatus(
                        InstallmentStatus.OVERDUE
                );

        for (LoanInstallment inst : overdueInstallments) {

            BigDecimal unpaid =
                    inst.getTotalAmount()
                            .subtract(inst.getPaidAmount());

            if (unpaid.compareTo(BigDecimal.ZERO) <= 0)
                continue;

            BigDecimal penalty =
                    unpaid.multiply(DAILY_PENALTY_RATE)
                            .setScale(2, RoundingMode.HALF_UP);

            inst.setPenaltyAmount(
                    inst.getPenaltyAmount().add(penalty)
            );

            inst.setTotalAmount(
                    inst.getTotalAmount().add(penalty)
            );
        }

        log.info("Daily penalty accrued for {} installments",
                overdueInstallments.size());
    }
}
