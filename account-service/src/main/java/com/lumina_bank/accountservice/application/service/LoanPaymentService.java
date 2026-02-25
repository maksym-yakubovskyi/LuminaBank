package com.lumina_bank.accountservice.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.lumina_bank.accountservice.domain.enums.InstallmentStatus;
import com.lumina_bank.accountservice.domain.enums.LoanStatus;
import com.lumina_bank.accountservice.domain.model.Account;
import com.lumina_bank.accountservice.domain.model.Loan;
import com.lumina_bank.accountservice.domain.model.LoanInstallment;
import com.lumina_bank.accountservice.domain.repository.LoanRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoanPaymentService {

    private final LoanRepository loanRepository;

    @Transactional(propagation = Propagation.MANDATORY)
    public void processCreditAccountPayment(Account creditAccount, BigDecimal amount) {

        if (amount.compareTo(BigDecimal.ZERO) <= 0)
            return;

        Loan loan = loanRepository
                .findByCreditAccountIdAndStatusIn(
                        creditAccount.getId(),
                        List.of(LoanStatus.ACTIVE, LoanStatus.OVERDUE)
                )
                .orElse(null);

        if (loan == null)
            return;

        log.info("Processing loan payment loanId={} accountId={} amount={}",
                loan.getId(),
                creditAccount.getId(),
                amount);

        BigDecimal remaining = amount;

        if (remaining.compareTo(loan.getRemainingPrincipal()) >= 0) {
            closeLoanFully(loan);
            return;
        }

        int fullyPaidCount = 0;

        List<LoanInstallment> installments =
                loan.getInstallments().stream()
                        .filter(i -> i.getStatus() != InstallmentStatus.PAID
                                && i.getStatus() != InstallmentStatus.CLOSED)
                        .sorted(Comparator.comparing(LoanInstallment::getDueDate))
                        .toList();

        for (LoanInstallment inst : installments) {

            if (remaining.compareTo(BigDecimal.ZERO) <= 0)
                break;

            BigDecimal paid = safe(inst.getPaidAmount());
            BigDecimal leftToPay = inst.getTotalAmount().subtract(paid);

            if (leftToPay.compareTo(BigDecimal.ZERO) <= 0)
                continue;

            BigDecimal payment = remaining.min(leftToPay);

            inst.setPaidAmount(inst.getPaidAmount().add(payment));
            remaining = remaining.subtract(payment);

            BigDecimal principalRatio =
                    inst.getPrincipalPart()
                            .divide(inst.getTotalAmount(), 6, RoundingMode.HALF_UP);

            BigDecimal principalPaid =
                    payment.multiply(principalRatio);

            loan.setRemainingPrincipal(
                    loan.getRemainingPrincipal().subtract(principalPaid)
            );

            if (inst.getPaidAmount()
                    .compareTo(inst.getTotalAmount()) == 0) {

                inst.setStatus(InstallmentStatus.PAID);
                inst.setPaidAt(LocalDate.now());
                fullyPaidCount++;

            } else {
                inst.setStatus(InstallmentStatus.PARTIALLY_PAID);
            }
        }

        if (fullyPaidCount > 1) {
            recalculateSchedule(loan);
        }

        if (loan.getRemainingPrincipal()
                .compareTo(BigDecimal.ZERO) <= 0) {

            loan.setRemainingPrincipal(BigDecimal.ZERO);
            loan.setStatus(LoanStatus.CLOSED);
            loan.setClosedAt(LocalDateTime.now());
            closeAllRemainingInstallments(loan);
        }

        if (loan.getStatus() == LoanStatus.CLOSED) {
            log.info("Loan fully closed loanId={}", loan.getId());
        }
    }

    // PRIVATE HELPERS

    private void recalculateSchedule(Loan loan) {
        List<LoanInstallment> future =
                loan.getInstallments().stream()
                        .filter(i -> i.getStatus() == InstallmentStatus.PENDING)
                        .sorted(Comparator.comparing(LoanInstallment::getDueDate))
                        .toList();

        BigDecimal remaining = loan.getRemainingPrincipal();

        BigDecimal monthlyRate =
                loan.getInterestRate()
                        .divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP)
                        .divide(BigDecimal.valueOf(12), 6, RoundingMode.HALF_UP);

        for (LoanInstallment inst : future) {

            BigDecimal interest = remaining.multiply(monthlyRate);
            BigDecimal principal =
                    loan.getMonthlyPayment().subtract(interest);

            if (principal.compareTo(remaining) > 0) {
                principal = remaining;
            }

            inst.setInterestPart(interest);
            inst.setPrincipalPart(principal);
            inst.setTotalAmount(principal.add(interest));

            remaining = remaining.subtract(principal);

            if (remaining.compareTo(BigDecimal.ZERO) <= 0)
                break;
        }
    }

    private void closeLoanFully(Loan loan) {
        loan.setRemainingPrincipal(BigDecimal.ZERO);
        loan.setStatus(LoanStatus.CLOSED);
        loan.setClosedAt(LocalDateTime.now());

        for (LoanInstallment inst : loan.getInstallments()) {
            if (inst.getStatus() != InstallmentStatus.PAID) {
                inst.setPaidAmount(inst.getTotalAmount());
                inst.setStatus(InstallmentStatus.PAID);
                inst.setPaidAt(LocalDate.now());
            }
        }
    }

    private void closeAllRemainingInstallments(Loan loan) {
        for (LoanInstallment inst : loan.getInstallments()) {
            if (inst.getStatus() != InstallmentStatus.PAID) {
                inst.setStatus(InstallmentStatus.CLOSED);
            }
        }
    }

    private BigDecimal safe(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }
}
