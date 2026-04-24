package com.lumina_bank.analyticsservice.application.analytics;

import com.lumina_bank.analyticsservice.api.response.analytics.AnalyticsCategoryResponse;
import com.lumina_bank.analyticsservice.api.response.analytics.AnalyticsDailyOverviewResponse;
import com.lumina_bank.analyticsservice.api.response.analytics.AnalyticsMonthlyOverviewResponse;
import com.lumina_bank.analyticsservice.api.response.analytics.AnalyticsTopRecipientResponse;
import com.lumina_bank.analyticsservice.application.service.UserContactInfoService;
import com.lumina_bank.analyticsservice.domain.model.AnalyticsDailySummary;
import com.lumina_bank.analyticsservice.domain.model.AnalyticsMonthlySummary;
import com.lumina_bank.analyticsservice.domain.model.AnalyticsTopRecipients;
import com.lumina_bank.analyticsservice.domain.model.AnalyticsTransactionEvent;
import com.lumina_bank.analyticsservice.domain.model.embedded.DailySummaryId;
import com.lumina_bank.analyticsservice.domain.model.embedded.MonthlySummaryId;
import com.lumina_bank.analyticsservice.domain.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsQueryService {
    private final AnalyticsDailySummaryRepository dailyRepo;
    private final AnalyticsTransactionEventRepository transactionEventRepo;
    private final AnalyticsMonthlySummaryRepository monthlyRepo;
    private final AnalyticsTopRecipientsRepository topRecipientsRepo;
    private final UserContactInfoService userContactInfoService;
    private final CategoryAnalyticsService categoryService;

    @Transactional(readOnly = true)
    public AnalyticsMonthlyOverviewResponse getMonthlyOverview(Long userId, Long accountId, YearMonth month) {
        MonthlySummaryId id = MonthlySummaryId.builder()
                .yearMonth(month)
                .userId(userId)
                .accountId(accountId)
                .build();

        AnalyticsMonthlySummary summary = monthlyRepo.findById(id)
                .orElse(AnalyticsMonthlySummary.builder()
                        .id(id)
                        .totalIncome(BigDecimal.ZERO)
                        .totalExpense(BigDecimal.ZERO)
                        .cashFlow(BigDecimal.ZERO)
                        .transactionCount(0L)
                        .build()
                );

        return AnalyticsMonthlyOverviewResponse.builder()
                .month(month)
                .totalIncome(summary.getTotalIncome())
                .totalExpense(summary.getTotalExpense())
                .cashFlow(summary.getCashFlow())
                .transactionCount(summary.getTransactionCount())
                .build();
    }

    @Transactional(readOnly = true)
    public AnalyticsDailyOverviewResponse getDailyOverview(Long userId, Long accountId, LocalDate date) {
        DailySummaryId id = DailySummaryId.builder()
                .date(date)
                .userId(userId)
                .accountId(accountId)
                .build();

        AnalyticsDailySummary summary = dailyRepo.findById(id)
                .orElse(AnalyticsDailySummary.builder()
                        .id(id)
                        .totalIncome(BigDecimal.ZERO)
                        .totalExpense(BigDecimal.ZERO)
                        .transactionCount(0L)
                        .build());

        return AnalyticsDailyOverviewResponse.builder()
                .date(date)
                .totalIncome(summary.getTotalIncome())
                .totalExpense(summary.getTotalExpense())
                .transactionCount(summary.getTransactionCount())
                .build();
    }

    @Transactional(readOnly = true)
    public List<AnalyticsTopRecipientResponse> getTopRecipients(Long userId){

        List<AnalyticsTopRecipients> recipients =
                topRecipientsRepo.findTop5ByUserIdOrderByTotalAmountDesc(userId);

        if (recipients.isEmpty()) {
            return List.of();
        }

        return recipients.stream()
                .map(r -> AnalyticsTopRecipientResponse.builder()
                        .recipientId(r.getRecipientId())
                        .displayName(userContactInfoService.getName(r.getRecipientId()))
                        .totalAmount(r.getTotalAmount())
                        .transactionCount(r.getTransactionCount())
                        .build()
                )
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AnalyticsTransactionEvent> getTransactionHistory(Long userId, LocalDate from, LocalDate to){
        LocalDateTime fromDateTime = from.atStartOfDay();
        LocalDateTime toDateTime = to.atTime(LocalTime.MAX);
        return transactionEventRepo.findByUserIdAndProcessedAtBetween(userId,fromDateTime,toDateTime);
    }

    @Transactional(readOnly = true)
    public List<AnalyticsDailySummary> getDailySummaries(Long userId,LocalDate from,LocalDate to){
        return dailyRepo.findAllByIdUserIdAndIdDateBetween(userId, from, to);
    }

    @Transactional(readOnly = true)
    public List<AnalyticsCategoryResponse> getCategoryExpenses(Long userId, YearMonth month) {
        return categoryService.buildCategoryStats(userId, month.atDay(1), month.atEndOfMonth());
    }

}
