package com.lumina_bank.analyticsservice.service;

import com.lumina_bank.analyticsservice.model.*;
import com.lumina_bank.analyticsservice.model.embedded.CategorySummaryId;
import com.lumina_bank.analyticsservice.model.embedded.DailySummaryId;
import com.lumina_bank.analyticsservice.model.embedded.MonthlySummaryId;
import com.lumina_bank.analyticsservice.model.embedded.RiskSummaryId;
import com.lumina_bank.analyticsservice.repository.*;
import com.lumina_bank.common.dto.event.payment_events.PaymentBlockedEvent;
import com.lumina_bank.common.dto.event.payment_events.PaymentCompletedEvent;
import com.lumina_bank.common.dto.event.payment_events.PaymentFlaggedEvent;
import com.lumina_bank.common.enums.payment.PaymentDirection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsService {
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
                        .accountId(e.fromAccountId())
                        .direction(PaymentDirection.OUTGOING.name())
                        .category(e.category())
                        .amount(e.amount())
                        .currency(e.fromCurrency())
                        .riskScore(e.riskScore())
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
                        .accountId(e.fromAccountId())
                        .direction(PaymentDirection.OUTGOING.name())
                        .category(e.category())
                        .amount(e.amount())
                        .currency(e.fromCurrency())
                        .riskScore(e.riskScore())
                        .status("BLOCKED")
                        .processedAt(e.blockedAt())
                        .build()
        );
    }

    private void updateDailySummary(PaymentCompletedEvent event) {
        LocalDate date = event.completedAt()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        DailySummaryId outId = DailySummaryId.builder()
                .date(date)
                .userId(event.initiatorUserId())
                .accountId(event.fromAccountId())
                .build();

        AnalyticsDailySummary outSummary = dailyRepo.findById(outId)
                .orElse(new AnalyticsDailySummary(outId,
                        BigDecimal.ZERO,
                        BigDecimal.ZERO,
                        0L,
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
                        0L,
                        0L));

        outSummary.setTotalExpense(outSummary.getTotalExpense().add(event.amount()));
        outSummary.setTransactionCount(outSummary.getTransactionCount() + 1);

        inSummary.setTotalIncome(inSummary.getTotalIncome().add(event.convertedAmount()));
        inSummary.setTransactionCount(inSummary.getTransactionCount() + 1);

        dailyRepo.save(outSummary);
        dailyRepo.save(inSummary);
    }

    private void updateCategorySummary(PaymentCompletedEvent event) {
        LocalDate date = event.completedAt().atZone(ZoneId.systemDefault()).toLocalDate();

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
        LocalDate date = event.completedAt().atZone(ZoneId.systemDefault()).toLocalDate();

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
                        0L,
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
                        0L,
                        0L));

        outSummary.setTotalExpense(outSummary.getTotalExpense().add(event.amount()));
        outSummary.setTransactionCount(outSummary.getTransactionCount() + 1);
        outSummary.setCashFlow(outSummary.getTotalIncome().subtract(outSummary.getTotalExpense()));

        inSummary.setTotalIncome(inSummary.getTotalIncome().add(event.amount()));
        inSummary.setTransactionCount(inSummary.getTransactionCount() + 1);
        inSummary.setCashFlow(inSummary.getTotalIncome().subtract(inSummary.getTotalExpense()));

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

    private void updateRiskSummary(Long userId, int riskScore, Instant time) {
        LocalDate date = time.atZone(ZoneId.systemDefault()).toLocalDate();

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

        summary.setFlaggedCount(summary.getFlaggedCount() + 1);
        summary.setMaxRiskScore(Math.max(summary.getMaxRiskScore(), riskScore));
        summary.setMinRiskScore(Math.min(summary.getMinRiskScore(), riskScore));
        summary.setAvgRiskScore((summary.getAvgRiskScore() + riskScore)/2);

        riskRepo.save(summary);
    }

}