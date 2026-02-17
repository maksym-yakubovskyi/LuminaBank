package com.lumina_bank.analyticsservice.service;

import com.lumina_bank.analyticsservice.dto.*;
import com.lumina_bank.analyticsservice.model.*;
import com.lumina_bank.analyticsservice.model.embedded.CategorySummaryId;
import com.lumina_bank.analyticsservice.model.embedded.DailySummaryId;
import com.lumina_bank.analyticsservice.model.embedded.MonthlySummaryId;
import com.lumina_bank.analyticsservice.model.embedded.RiskSummaryId;
import com.lumina_bank.analyticsservice.repository.*;
import com.lumina_bank.analyticsservice.service.client.UserServiceClient;
import com.lumina_bank.common.dto.event.payment_events.PaymentBlockedEvent;
import com.lumina_bank.common.dto.event.payment_events.PaymentCompletedEvent;
import com.lumina_bank.common.dto.event.payment_events.PaymentFlaggedEvent;
import com.lumina_bank.common.enums.payment.PaymentDirection;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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

    private final UserServiceClient  userServiceClient;

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
    public List<AnalyticsCategoryResponse> getCategoryExpenses(Long userId, YearMonth month) {
        LocalDate from = month.atDay(1);
        LocalDate to = month.atEndOfMonth();

        List<AnalyticsCategorySummary> summaries =
                categorySummaryRepo.findAllByIdUserIdAndIdDateBetween(userId, from, to);

        if (summaries.isEmpty()) {
            return List.of();
        }

        BigDecimal totalExpense = summaries.stream()
                .map(AnalyticsCategorySummary::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalExpense.compareTo(BigDecimal.ZERO) == 0) {
            return List.of();
        }

        return summaries.stream()
                .collect(Collectors.groupingBy(
                        s->s.getId().getCategory(),
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                AnalyticsCategorySummary::getTotalAmount,
                                BigDecimal::add
                        )
                ))
                .entrySet()
                .stream()
                .map(
                        entry->{
                            BigDecimal amount = entry.getValue();

                            int percent = amount
                                    .multiply(BigDecimal.valueOf(100))
                                    .divide(totalExpense,0, RoundingMode.HALF_UP)
                                    .intValue();

                            return AnalyticsCategoryResponse.builder()
                                    .category(entry.getKey())
                                    .totalAmount(amount)
                                    .percentage(percent)
                                    .build();
                        }
                )
                .sorted((a,b)-> b.totalAmount().compareTo(a.totalAmount()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AnalyticsCategoryResponse> getTopCategoriesForPeriod(
            Long userId,
            YearMonth fromMonth,
            YearMonth toMonth
    ) {
        LocalDate from = fromMonth.atDay(1);
        LocalDate to = toMonth.atEndOfMonth();

        List<AnalyticsCategorySummary> summaries =
                categorySummaryRepo.findAllByIdUserIdAndIdDateBetween(userId, from, to);

        if (summaries.isEmpty()) {
            return List.of();
        }

        BigDecimal totalExpense = summaries.stream()
                .map(AnalyticsCategorySummary::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalExpense.compareTo(BigDecimal.ZERO) == 0) {
            return List.of();
        }

        return summaries.stream()
                .collect(Collectors.groupingBy(
                        s->s.getId().getCategory(),
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                AnalyticsCategorySummary::getTotalAmount,
                                BigDecimal::add
                        )
                ))
                .entrySet()
                .stream()
                .map(
                        entry->{
                            BigDecimal amount = entry.getValue();

                            int percent = amount
                                    .multiply(BigDecimal.valueOf(100))
                                    .divide(totalExpense,0, RoundingMode.HALF_UP)
                                    .intValue();

                            return AnalyticsCategoryResponse.builder()
                                    .category(entry.getKey())
                                    .totalAmount(amount)
                                    .percentage(percent)
                                    .build();
                        }
                )
                .sorted((a,b)-> b.totalAmount().compareTo(a.totalAmount()))
                .toList();
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
                        .displayName(resolveRecipientName(r.getRecipientId()))
                        .totalAmount(r.getTotalAmount())
                        .transactionCount(r.getTransactionCount())
                        .build()
                )
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AnalyticsDailySummary> getDailySummaries(Long userId,LocalDate from,LocalDate to){
        return dailyRepo.findAllByIdUserIdAndIdDateBetween(userId, from, to);
    }

    @Transactional(readOnly = true)
    public List<AnalyticsTransactionEvent> getTransactionHistory(Long userId, LocalDate from, LocalDate to){
        LocalDateTime fromDateTime = from.atStartOfDay();
        LocalDateTime toDateTime = to.atTime(LocalTime.MAX);
        return transactionEventRepo.findByUserIdAndProcessedAtBetween(userId,fromDateTime,toDateTime);
    }

    @Transactional(readOnly = true)
    public RecommendationResponse buildRecommendationInfo (Long userId){
        YearMonth currentMonth = YearMonth.now();
        YearMonth fromMonth = currentMonth.minusMonths(5);

        List<AnalyticsMonthlySummary> monthlySummaries =
                monthlyRepo.findByIdUserIdAndIdYearMonthBetween(
                        userId,
                        fromMonth,
                        currentMonth
                );

        if (monthlySummaries.isEmpty()) {
            return emptyRec();
        }

        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpense = BigDecimal.ZERO;
        BigDecimal totalCashFlow = BigDecimal.ZERO;
        long totalTransactions = 0;

        for (AnalyticsMonthlySummary m : monthlySummaries) {
            totalIncome = totalIncome.add(nullSafe(m.getTotalIncome()));
            totalExpense = totalExpense.add(nullSafe(m.getTotalExpense()));
            totalCashFlow = totalCashFlow.add(nullSafe(m.getCashFlow()));
            totalTransactions += nullSafe(m.getTransactionCount());
        }

        int monthsCount = monthlySummaries.size();

        BigDecimal avgIncome = divide(totalIncome, monthsCount);
        BigDecimal avgExpense = divide(totalExpense, monthsCount);
        BigDecimal avgCashFlow = divide(totalCashFlow, monthsCount);

        int monthlyTransactionCount =
                Math.toIntExact(
                        Math.round(
                                (double) totalTransactions / monthsCount
                        )
                );

        BigDecimal totalVolume = totalIncome.add(totalExpense);

        BigDecimal avgTransactionAmount =
                totalTransactions == 0
                        ? BigDecimal.ZERO
                        : totalVolume.divide(
                        BigDecimal.valueOf(totalTransactions),
                        2,
                        RoundingMode.HALF_UP
                );

        BigDecimal expenseGrowth = BigDecimal.ZERO;
        BigDecimal incomeGrowth = BigDecimal.ZERO;

        monthlySummaries.sort(Comparator.comparing(m -> m.getId().getYearMonth()));

        if (monthlySummaries.size() >= 2) {
            AnalyticsMonthlySummary last =
                    monthlySummaries.getLast();

            AnalyticsMonthlySummary prev =
                    monthlySummaries.get(monthlySummaries.size() - 2);

            expenseGrowth = growthPercent(
                    nullSafe(prev.getTotalExpense()),
                    nullSafe(last.getTotalExpense())
            );

            incomeGrowth = growthPercent(
                    nullSafe(prev.getTotalIncome()),
                    nullSafe(last.getTotalIncome())
            );
        }

        List<AnalyticsCategoryResponse> topCategories =
                getTopCategoriesForPeriod(userId, fromMonth,currentMonth).stream().limit(5).toList();

        List<AnalyticsTopRecipientResponse> topRecipients =
                getTopRecipients(userId).stream().limit(5).toList();

        return RecommendationResponse.builder()
                .avgMonthlyExpense(avgExpense)
                .avgMonthlyIncome(avgIncome)
                .avgMonthlyCashFlow(avgCashFlow)
                .expenseGrowthPercent(expenseGrowth)
                .incomeGrowthPercent(incomeGrowth)
                .topCategories(topCategories)
                .avgTransactionAmount(avgTransactionAmount)
                .monthlyTransactionCount(monthlyTransactionCount)
                .topRecipients(topRecipients)
                .build();
    }

    @Transactional(readOnly = true)
    public AiForecastResponse buildForecast(Long userId) {

        YearMonth currentMonth = YearMonth.now();
        YearMonth fromMonth = currentMonth.minusMonths(5);

        List<AnalyticsMonthlySummary> summaries =
                monthlyRepo.findByIdUserIdAndIdYearMonthBetween(
                        userId,
                        fromMonth,
                        currentMonth
                );

        if (summaries.size() < 2) {
            return emptyForecast();
        }

        summaries.sort(
                Comparator.comparing(m -> m.getId().getYearMonth())
        );

        int months = summaries.size();

        List<BigDecimal> expenses = summaries.stream()
                .map(m -> nullSafe(m.getTotalExpense()))
                .toList();

        List<BigDecimal> incomes = summaries.stream()
                .map(m -> nullSafe(m.getTotalIncome()))
                .toList();

        BigDecimal predictedExpense = linearForecast(expenses);
        BigDecimal predictedIncome = linearForecast(incomes);

        BigDecimal predictedCashFlow =
                predictedIncome.subtract(predictedExpense);

        BigDecimal expenseTrendPercent =
                growthPercent(expenses.getFirst(),
                        expenses.getLast());

        BigDecimal incomeTrendPercent =
                growthPercent(incomes.getFirst(),
                        incomes.getLast());

        return new AiForecastResponse(
                predictedIncome,
                predictedExpense,
                predictedCashFlow,
                expenseTrendPercent,
                incomeTrendPercent,
                months
        );
    }

    private BigDecimal nullSafe(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
    private long nullSafe(Long value) {
        return value == null ? 0L : value;
    }

    private RecommendationResponse emptyRec() {
        return new RecommendationResponse(
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                List.of(),
                BigDecimal.ZERO,
                0,
                List.of()
        );
    }

    private AiForecastResponse emptyForecast() {
        return new AiForecastResponse(
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                0
        );
    }

    private BigDecimal divide(BigDecimal value, int divisor) {
        if (divisor == 0) return BigDecimal.ZERO;
        return value.divide(
                BigDecimal.valueOf(divisor),
                2,
                RoundingMode.HALF_UP
        );
    }

    private BigDecimal growthPercent(BigDecimal previous, BigDecimal current) {
        if (previous.compareTo(BigDecimal.ZERO) == 0) {
            return current.compareTo(BigDecimal.ZERO) == 0
                    ? BigDecimal.ZERO
                    : BigDecimal.valueOf(100);
        }

        return current.subtract(previous)
                .multiply(BigDecimal.valueOf(100))
                .divide(previous, 2, RoundingMode.HALF_UP);
    }


    private String resolveRecipientName(Long recipientId){
        try{
            var bResponse = userServiceClient
                    .getBusinessUserNameById(recipientId);

            if(bResponse.getStatusCode().is2xxSuccessful()
                    && bResponse.getBody()!=null
                    && !bResponse.getBody().isBlank()){
                return bResponse.getBody();
            }
        }catch (FeignException.NotFound e){
            // не бізнес-користувач
            log.warn("Business User service unavailable, recipientId={}", recipientId);
        }

        try {
            var userResponse =
                    userServiceClient.getUserNameById(recipientId);

            if (userResponse.getStatusCode().is2xxSuccessful()
                    && userResponse.getBody() != null
                    && !userResponse.getBody().isBlank()) {

                return userResponse.getBody();
            }
        } catch (FeignException.NotFound e) {
            // не звичайний користувач
            log.warn("User service unavailable, recipientId={}", recipientId);
        }

        return "Recipient #" + recipientId;
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

        summary.setFlaggedCount(summary.getFlaggedCount() + 1);
        summary.setMaxRiskScore(Math.max(summary.getMaxRiskScore(), riskScore));
        summary.setMinRiskScore(Math.min(summary.getMinRiskScore(), riskScore));
        summary.setAvgRiskScore((summary.getAvgRiskScore() + riskScore)/2);

        riskRepo.save(summary);
    }

    private BigDecimal linearForecast(List<BigDecimal> values) {

        int n = values.size();

        if (n < 2) {
            return values.isEmpty() ? BigDecimal.ZERO : values.getFirst();
        }

        double sumX = 0;
        double sumY = 0;
        double sumXY = 0;
        double sumXX = 0;

        for (int i = 0; i < n; i++) {
            double x = i + 1;
            double y = values.get(i).doubleValue();

            sumX += x;
            sumY += y;
            sumXY += x * y;
            sumXX += x * x;
        }

        double slope =
                (n * sumXY - sumX * sumY) /
                        (n * sumXX - sumX * sumX);

        double intercept =
                (sumY - slope * sumX) / n;

        double nextX = n + 1;

        double forecast = intercept + slope * nextX;

        return BigDecimal.valueOf(forecast)
                .setScale(2, RoundingMode.HALF_UP);
    }
}