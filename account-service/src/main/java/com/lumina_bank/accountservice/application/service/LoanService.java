package com.lumina_bank.accountservice.application.service;

import com.lumina_bank.accountservice.api.request.LoanApplicationRequest;
import com.lumina_bank.accountservice.api.response.LoanOfferResponse;
import com.lumina_bank.accountservice.domain.exception.ActiveLoanAlreadyExistsException;
import com.lumina_bank.accountservice.domain.exception.InvalidLoanAmountException;
import com.lumina_bank.accountservice.domain.exception.InvalidLoanTermException;
import com.lumina_bank.accountservice.domain.exception.LoanNotApprovedException;
import com.lumina_bank.accountservice.infrastructure.external.analytics.dto.LoanInfoExternalResponse;
import com.lumina_bank.accountservice.domain.enums.InstallmentStatus;
import com.lumina_bank.accountservice.domain.enums.LoanStatus;
import com.lumina_bank.accountservice.domain.model.Account;
import com.lumina_bank.accountservice.domain.model.Loan;
import com.lumina_bank.accountservice.domain.model.LoanInstallment;
import com.lumina_bank.accountservice.domain.repository.LoanRepository;
import com.lumina_bank.accountservice.infrastructure.external.analytics.AnalyticsServiceClient;
import com.lumina_bank.common.exception.ExternalServiceException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoanService {
    private final LoanRepository loanRepository;
    private final AnalyticsServiceClient analyticsClient;
    private final AccountService accountService;

    private static final BigDecimal MAX_PAYMENT_RATIO = new BigDecimal("0.40");

    private static final int MIN_TERM = 3;
    private static final int MAX_TERM = 48;

    @Transactional(readOnly = true)
    public List<LoanOfferResponse> generateLoanOffers(Long userId, Long creditAccountId, BigDecimal requestedAmount) {
        if (requestedAmount == null || requestedAmount.compareTo(BigDecimal.ZERO) <= 0)
            throw new InvalidLoanAmountException("Requested amount must be positive");

        Account creditAccount =
                accountService.getCreditAccountById(userId, creditAccountId);

        ensureNoActiveLoan(creditAccount.getId());

        LoanInfoExternalResponse info = fetchAnalytics();
        List<Integer> terms = List.of(3, 6, 12, 24, 36, 48);

        return terms.stream()
                .map(term -> buildOffer(requestedAmount, term, info))
                .filter(LoanOfferResponse::approved)
                .sorted(Comparator.comparing(LoanOfferResponse::monthlyPayment))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Loan> getActiveLoans(Long userId) {

        return loanRepository
                .findAllByCreditAccount_UserIdAndStatusIn(
                        userId,
                        List.of(LoanStatus.ACTIVE, LoanStatus.OVERDUE)
                );
    }

    @Transactional(readOnly = true)
    public List<Account> getAvailableCreditAccounts(Long userId) {

        List<Account> creditAccounts =
                accountService.getCreditAccountsEntities(userId);

        return creditAccounts.stream()
                .filter(acc ->
                        !loanRepository.existsByCreditAccountIdAndStatusIn(
                                acc.getId(),
                                List.of(LoanStatus.ACTIVE, LoanStatus.OVERDUE)
                        )
                )
                .toList();
    }

    @Transactional
    public Loan approveLoan(Long userId, LoanApplicationRequest request) {

        if (request.requestedTermMonths() < MIN_TERM || request.requestedTermMonths() > MAX_TERM)
            throw new InvalidLoanTermException("Loan term must be between " + MIN_TERM + " and " + MAX_TERM + " months");

        Account creditAccount = accountService.getCreditAccountById(userId, request.creditAccountId());

        ensureNoActiveLoan(creditAccount.getId());

        LoanInfoExternalResponse info = fetchAnalytics();

        LoanOfferResponse offer =
                buildOffer(
                        request.requestedAmount(),
                        request.requestedTermMonths(),
                        info
                );

        if (!offer.approved())
            throw new LoanNotApprovedException("Loan conditions not satisfied for requested parameters");

        Loan loan = createLoanEntity(creditAccount, offer);

        loanRepository.save(loan);

        // зарахування коштів
        accountService.creditAccount(
                creditAccount.getId(),
                offer.approvedAmount()
        );


        return loan;
    }

    // PRIVATE HELPERS

    private LoanOfferResponse buildOffer(
            BigDecimal amount,
            int term,
            LoanInfoExternalResponse info
    ) {

        int riskScore = calculateRisk(info);
        BigDecimal interestRate = determineInterestRate(riskScore);

        BigDecimal monthlyPayment =
                calculateMonthlyPayment(amount, interestRate, term);

        BigDecimal maxAllowed =
                safe(info.avgMonthlyIncome())
                        .multiply(MAX_PAYMENT_RATIO);

        if (monthlyPayment.compareTo(maxAllowed) > 0
                || safe(info.avgMonthlyCashFlow())
                .compareTo(BigDecimal.ZERO) <= 0) {

            return LoanOfferResponse.builder()
                    .approved(false)
                    .riskScore(riskScore)
                    .build();
        }

        return LoanOfferResponse.builder()
                .approved(true)
                .approvedAmount(amount)
                .interestRate(interestRate)
                .termMonths(term)
                .monthlyPayment(monthlyPayment)
                .totalPayable(
                        monthlyPayment.multiply(BigDecimal.valueOf(term))
                )
                .riskScore(riskScore)
                .build();
    }

    private Loan createLoanEntity(
            Account creditAccount,
            LoanOfferResponse offer
    ) {

        Loan loan = Loan.builder()
                .creditAccount(creditAccount)
                .principalAmount(offer.approvedAmount())
                .interestRate(offer.interestRate())
                .termMonths(offer.termMonths())
                .monthlyPayment(offer.monthlyPayment())
                .remainingPrincipal(offer.approvedAmount())
                .totalPayableAmount(offer.totalPayable())
                .totalInterestAmount(
                        offer.totalPayable()
                                .subtract(offer.approvedAmount())
                )
                .riskScore(offer.riskScore())
                .status(LoanStatus.ACTIVE)
                .approvedAt(LocalDateTime.now())
                .build();

        generateInstallments(loan);

        return loan;
    }

    private void generateInstallments(Loan loan) {
        BigDecimal remaining =
                loan.getPrincipalAmount();

        BigDecimal monthlyRate =
                loan.getInterestRate()
                        .divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP)
                        .divide(BigDecimal.valueOf(12), 6, RoundingMode.HALF_UP);

        List<LoanInstallment> list = new ArrayList<>();

        for (int i = 1; i <= loan.getTermMonths(); i++) {

            BigDecimal interestPart =
                    remaining.multiply(monthlyRate);

            BigDecimal principalPart =
                    loan.getMonthlyPayment()
                            .subtract(interestPart);

            remaining = remaining.subtract(principalPart);

            LoanInstallment installment =
                    LoanInstallment.builder()
                            .loan(loan)
                            .installmentNumber(i)
                            .dueDate(LocalDate.now().plusMonths(i))
                            .principalPart(principalPart)
                            .interestPart(interestPart)
                            .totalAmount(loan.getMonthlyPayment())
                            .paidAmount(BigDecimal.ZERO)
                            .status(InstallmentStatus.PENDING)
                            .build();

            list.add(installment);
        }

        loan.setInstallments(list);
    }

    private int calculateRisk(LoanInfoExternalResponse inf) {
        int score = 0;

        if (safe(inf.avgMonthlyCashFlow()).compareTo(BigDecimal.ZERO) < 0)
            score += 40;

        if (safe(inf.expenseGrowthPercent())
                .compareTo(BigDecimal.valueOf(30)) > 0)
            score += 20;

        if (inf.monthlyTransactionCount() != null
                && inf.monthlyTransactionCount() < 5)
            score += 10;

        return score;
    }

    private BigDecimal determineInterestRate(int riskScore) {
        if (riskScore < 30) return new BigDecimal("12.00");
        if (riskScore < 60) return new BigDecimal("18.00");
        return new BigDecimal("25.00");
    }

    private BigDecimal calculateMonthlyPayment(
            BigDecimal principal,
            BigDecimal annualRate,
            int months
    ) {

        BigDecimal monthlyRate =
                annualRate
                        .divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP)
                        .divide(BigDecimal.valueOf(12), 6, RoundingMode.HALF_UP);

        BigDecimal numerator = principal.multiply(monthlyRate);

        BigDecimal denominator =
                BigDecimal.ONE.subtract(
                        BigDecimal.ONE
                                .add(monthlyRate)
                                .pow(-months, new MathContext(10))
                );

        return numerator.divide(denominator, 2, RoundingMode.HALF_UP);
    }

    private void ensureNoActiveLoan(Long accountId) {
        boolean exists = loanRepository
                .existsByCreditAccountIdAndStatusIn(
                        accountId,
                        List.of(LoanStatus.ACTIVE, LoanStatus.OVERDUE)
                );

        if (exists)
            throw new ActiveLoanAlreadyExistsException("Active loan already exists for this credit account");
    }

    private LoanInfoExternalResponse fetchAnalytics() {
        try {
            LoanInfoExternalResponse body = analyticsClient.getLoanInfo();

            if (body == null) throw new ExternalServiceException("Analytics returned empty body");
            return body;

        } catch (FeignException e) {
            log.error("Analytics service error status={}", e.status());
            throw new ExternalServiceException("Analytics unavailable", e);

        } catch (Exception e) {
            log.error("Unexpected analytics error", e);
            throw new ExternalServiceException("Risk evaluation failed", e);
        }
    }

    private BigDecimal safe(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }
}
