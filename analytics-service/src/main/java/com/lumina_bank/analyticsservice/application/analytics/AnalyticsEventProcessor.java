package com.lumina_bank.analyticsservice.application.analytics;

import com.lumina_bank.analyticsservice.domain.enums.PaymentDirection;
import com.lumina_bank.analyticsservice.domain.model.*;
import com.lumina_bank.analyticsservice.domain.model.embedded.CategorySummaryId;
import com.lumina_bank.analyticsservice.domain.model.embedded.DailySummaryId;
import com.lumina_bank.analyticsservice.domain.model.embedded.MonthlySummaryId;
import com.lumina_bank.analyticsservice.domain.model.embedded.RiskSummaryId;
import com.lumina_bank.analyticsservice.domain.repository.*;
import com.lumina_bank.common.dto.event.payment_events.PaymentBlockedEvent;
import com.lumina_bank.common.dto.event.payment_events.PaymentCompletedEvent;
import com.lumina_bank.common.dto.event.payment_events.PaymentFlaggedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsEventProcessor {
    private final AnalyticsDailySummaryRepository dailyRepo;
    private final AnalyticsTransactionEventRepository transactionEventRepo;
    private final AnalyticsCategorySummaryRepository categorySummaryRepo;
    private final AnalyticsMonthlySummaryRepository monthlyRepo;
    private final AnalyticsRiskSummaryRepository riskRepo;
    private final AnalyticsTopRecipientsRepository topRecipientsRepo;

    @Transactional
    public void handlePaymentCompleted(PaymentCompletedEvent e) {
        saveRawEvent(e);
        updateDailySummary(e);
        updateCategorySummary(e);
        updateMonthlySummary(e);
        updateTopRecipients(e);
    }

    @Transactional
    public void handlePaymentFlagged(PaymentFlaggedEvent e) {
        saveFlaggedRawEvent(e);
        updateRiskSummary(e.initiatorUserId(), e.riskScore(), e.flaggedAt());
    }

    @Transactional
    public void handlePaymentBlocked(PaymentBlockedEvent e) {
        saveBlockedRawEvent(e);
        updateRiskSummary(e.initiatorUserId(), e.riskScore(), e.blockedAt());
    }

    private void saveRawEvent(PaymentCompletedEvent event) {
        // 1)OUTGOING — ініціатор
        transactionEventRepo.save(
                AnalyticsTransactionEvent.builder()
                        .paymentId(event.paymentId())
                        .userId(event.initiatorUserId())
                        .accountId(event.fromAccountId())
                        .direction(PaymentDirection.OUTGOING.name())
                        .category(event.category())
                        .amount(event.amount())
                        .currency(event.fromCurrency())
                        .riskScore(null)
                        .status("SUCCESS")
                        .processedAt(event.completedAt())
                        .build()
        );

        // 2) INCOMING — отримувач
        transactionEventRepo.save(
                AnalyticsTransactionEvent.builder()
                        .paymentId(event.paymentId())
                        .userId(event.toAccountOwnerId())
                        .accountId(event.toAccountId())
                        .direction(PaymentDirection.INCOMING.name())
                        .category(event.category())
                        .amount(event.convertedAmount())
                        .currency(event.toCurrency())
                        .riskScore(null)
                        .status("SUCCESS")
                        .processedAt(event.completedAt())
                        .build()
        );
    }


    private void saveFlaggedRawEvent(PaymentFlaggedEvent e) {
        transactionEventRepo.save(
                AnalyticsTransactionEvent.builder()
                        .paymentId(e.paymentId())
                        .userId(e.initiatorUserId())
                        .direction(PaymentDirection.OUTGOING.name())
                        .category(e.category())
                        .amount(e.amount())
                        .riskScore(e.riskScore())
                        .reasons(String.join(",", e.reasons()))
                        .status("FLAGGED")
                        .processedAt(e.flaggedAt())
                        .build()
        );
    }

    private void saveBlockedRawEvent(PaymentBlockedEvent e) {
        transactionEventRepo.save(
                AnalyticsTransactionEvent.builder()
                        .paymentId(e.paymentId())
                        .userId(e.initiatorUserId())
                        .direction(PaymentDirection.OUTGOING.name())
                        .category(e.category())
                        .amount(e.amount())
                        .riskScore(e.riskScore())
                        .reasons(String.join(",", e.reasons()))
                        .status("BLOCKED")
                        .processedAt(e.blockedAt())
                        .build()
        );
    }

    private void updateDailySummary(PaymentCompletedEvent event) {
        LocalDate date = event.completedAt().toLocalDate();

        DailySummaryId outId = DailySummaryId.builder()
                .date(date)
                .userId(event.initiatorUserId())
                .accountId(event.fromAccountId())
                .build();

        AnalyticsDailySummary outSummary = dailyRepo.findById(outId)
                .orElse(new AnalyticsDailySummary(outId,
                        BigDecimal.ZERO,
                        BigDecimal.ZERO,
                        0L));

        DailySummaryId inId = DailySummaryId.builder()
                .date(date)
                .userId(event.toAccountOwnerId())
                .accountId(event.toAccountId())
                .build();

        AnalyticsDailySummary inSummary = dailyRepo.findById(inId)
                .orElse(new AnalyticsDailySummary(inId,
                        BigDecimal.ZERO,
                        BigDecimal.ZERO,
                        0L));

        outSummary.setTotalExpense(outSummary.getTotalExpense().add(event.amount()));
        outSummary.setTransactionCount(outSummary.getTransactionCount() + 1);

        inSummary.setTotalIncome(inSummary.getTotalIncome().add(event.convertedAmount()));
        inSummary.setTransactionCount(inSummary.getTransactionCount() + 1);

        dailyRepo.save(outSummary);
        dailyRepo.save(inSummary);
    }

    private void updateCategorySummary(PaymentCompletedEvent event) {
        LocalDate date = event.completedAt().toLocalDate();

        CategorySummaryId id = CategorySummaryId.builder()
                .date(date)
                .userId(event.initiatorUserId())
                .category(event.category())
                .build();

        AnalyticsCategorySummary summary = categorySummaryRepo.findById(id)
                .orElse(new AnalyticsCategorySummary(id,BigDecimal.ZERO,0L));

        summary.setTotalAmount(summary.getTotalAmount().add(event.amount()));
        summary.setTransactionCount(summary.getTransactionCount() + 1);

        categorySummaryRepo.save(summary);
    }

    private void updateMonthlySummary(PaymentCompletedEvent event) {
        LocalDate date = event.completedAt().toLocalDate();

        MonthlySummaryId outId = MonthlySummaryId.builder()
                .yearMonth(YearMonth.from(date))
                .userId(event.initiatorUserId())
                .accountId(event.fromAccountId())
                .build();

        AnalyticsMonthlySummary outSummary = monthlyRepo.findById(outId)
                .orElse(new AnalyticsMonthlySummary(outId,
                        BigDecimal.ZERO,
                        BigDecimal.ZERO,
                        BigDecimal.ZERO,
                        0L));

        MonthlySummaryId inId = MonthlySummaryId.builder()
                .yearMonth(YearMonth.from(date))
                .userId(event.toAccountOwnerId())
                .accountId(event.toAccountId())
                .build();

        AnalyticsMonthlySummary inSummary = monthlyRepo.findById(inId)
                .orElse(new AnalyticsMonthlySummary(inId,
                        BigDecimal.ZERO,
                        BigDecimal.ZERO,
                        BigDecimal.ZERO,
                        0L));

        outSummary.setTotalExpense(outSummary.getTotalExpense().add(event.amount()));
        outSummary.setTransactionCount(outSummary.getTransactionCount() + 1);

        BigDecimal updatedIncome = nullSafe(outSummary.getTotalIncome());
        BigDecimal updatedExpense = nullSafe(outSummary.getTotalExpense());

        outSummary.setCashFlow(updatedIncome.subtract(updatedExpense));

        inSummary.setTotalIncome(inSummary.getTotalIncome().add(event.convertedAmount()));
        inSummary.setTransactionCount(inSummary.getTransactionCount() + 1);

        BigDecimal updatedIncomeIn = nullSafe(inSummary.getTotalIncome());
        BigDecimal updatedExpenseIn = nullSafe(inSummary.getTotalExpense());

        inSummary.setCashFlow(updatedIncomeIn.subtract(updatedExpenseIn));

        monthlyRepo.save(outSummary);
        monthlyRepo.save(inSummary);
    }

    private void updateTopRecipients(PaymentCompletedEvent event) {
        AnalyticsTopRecipients recipient = topRecipientsRepo.findByUserIdAndRecipientId(event.initiatorUserId(),event.toAccountOwnerId())
                .orElse(AnalyticsTopRecipients.builder()
                        .userId(event.initiatorUserId())
                        .recipientId(event.toAccountOwnerId())
                        .totalAmount(BigDecimal.ZERO)
                        .transactionCount(0L)
                        .build());

        recipient.setTotalAmount(recipient.getTotalAmount().add(event.amount()));
        recipient.setTransactionCount(recipient.getTransactionCount() + 1);

        recipient.setLastTransactionAt(event.completedAt());

        topRecipientsRepo.save(recipient);
    }

    private void updateRiskSummary(Long userId, int riskScore, LocalDateTime time) {
        LocalDate date = time.toLocalDate();

        RiskSummaryId id = RiskSummaryId.builder()
                .date(date)
                .userId(userId)
                .build();

        AnalyticsRiskSummary summary = riskRepo.findById(id)
                .orElse(new AnalyticsRiskSummary(id,
                        riskScore,
                        riskScore,
                        riskScore,
                        0));

        summary.setMaxRiskScore(Math.max(summary.getMaxRiskScore(), riskScore));
        summary.setMinRiskScore(Math.min(summary.getMinRiskScore(), riskScore));

        int newCount = summary.getFlaggedCount() + 1;

        int newAvg = (
                summary.getAvgRiskScore() * summary.getFlaggedCount()
                        + riskScore
        ) / newCount;

        summary.setAvgRiskScore(newAvg);
        summary.setFlaggedCount(newCount);

        riskRepo.save(summary);
    }

    private BigDecimal nullSafe(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

}
