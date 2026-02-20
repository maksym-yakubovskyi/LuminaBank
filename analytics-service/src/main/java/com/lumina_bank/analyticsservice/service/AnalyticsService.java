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
        return buildCategoryStats(userId, month.atDay(1), month.atEndOfMonth());
    }

    @Transactional(readOnly = true)
    public List<AnalyticsCategoryResponse> getTopCategoriesForPeriod(
            Long userId,
            YearMonth fromMonth,
            YearMonth toMonth
    ) {
        return buildCategoryStats(userId, fromMonth.atDay(1), toMonth.atEndOfMonth());
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
    public LoanInfoResponse buildLoanInfo(Long userId){
        FinancialAggregate agg = aggregateLastMonths(userId, 5);
        return LoanInfoResponse.from(agg);
    }

    @Transactional(readOnly = true)
    public RecommendationResponse buildRecommendationInfo (Long userId){
        YearMonth currentMonth = YearMonth.now();
        YearMonth fromMonth = currentMonth.minusMonths(5);

        FinancialAggregate agg = aggregateLastMonths(userId, 5);

        List<AnalyticsCategoryResponse> topCategories =
                getTopCategoriesForPeriod(userId, fromMonth,currentMonth).stream().limit(5).toList();

        List<AnalyticsTopRecipientResponse> topRecipients =
                getTopRecipients(userId).stream().limit(5).toList();

        return RecommendationResponse.builder()
                .avgMonthlyExpense(agg.getAvgExpense())
                .avgMonthlyIncome(agg.getAvgIncome())
                .avgMonthlyCashFlow(agg.getAvgCashFlow())
                .expenseGrowthPercent(agg.getExpenseGrowth())
                .incomeGrowthPercent(agg.getIncomeGrowth())
                .avgTransactionAmount(agg.getAvgTransactionAmount())
                .monthlyTransactionCount(agg.getMonthlyTransactionCount())
                .topCategories(topCategories)
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

    private BigDecimal linearForecast(List<BigDecimal> values) {

        int n = values.size();

        if (n == 0) {
            return BigDecimal.ZERO;
        }

        if (n == 1) {
            return values.getFirst().setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal sumX = BigDecimal.ZERO;
        BigDecimal sumY = BigDecimal.ZERO;
        BigDecimal sumXY = BigDecimal.ZERO;
        BigDecimal sumXX = BigDecimal.ZERO;

        for (int i = 0; i < n; i++) {

            BigDecimal x = BigDecimal.valueOf(i + 1);
            BigDecimal y = values.get(i);

            sumX = sumX.add(x);
            sumY = sumY.add(y);
            sumXY = sumXY.add(x.multiply(y));
            sumXX = sumXX.add(x.multiply(x));
        }

        BigDecimal bigN = BigDecimal.valueOf(n);

        BigDecimal numerator =
                bigN.multiply(sumXY)
                        .subtract(sumX.multiply(sumY));

        BigDecimal denominator =
                bigN.multiply(sumXX)
                        .subtract(sumX.multiply(sumX));

        if (denominator.compareTo(BigDecimal.ZERO) == 0) {
            return values.get(n - 1).setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal slope = numerator.divide(
                denominator,
                10,
                RoundingMode.HALF_UP
        );

        BigDecimal intercept = sumY
                .subtract(slope.multiply(sumX))
                .divide(bigN, 10, RoundingMode.HALF_UP);

        BigDecimal nextX = BigDecimal.valueOf(n + 1);

        BigDecimal forecast = intercept.add(
                slope.multiply(nextX)
        );

        return forecast.setScale(2, RoundingMode.HALF_UP);
    }

    private FinancialAggregate aggregateLastMonths(Long userId, int monthsBack) {

        YearMonth currentMonth = YearMonth.now();
        YearMonth fromMonth = currentMonth.minusMonths(monthsBack);

        List<AnalyticsMonthlySummary> summaries =
                monthlyRepo.findByIdUserIdAndIdYearMonthBetween(
                        userId,
                        fromMonth,
                        currentMonth
                );

        if (summaries.isEmpty()) {
            return FinancialAggregate.empty();
        }

        summaries.sort(Comparator.comparing(m -> m.getId().getYearMonth()));

        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpense = BigDecimal.ZERO;
        BigDecimal totalCashFlow = BigDecimal.ZERO;
        long totalTransactions = 0;

        for (AnalyticsMonthlySummary m : summaries) {
            totalIncome = totalIncome.add(nullSafe(m.getTotalIncome()));
            totalExpense = totalExpense.add(nullSafe(m.getTotalExpense()));
            totalCashFlow = totalCashFlow.add(nullSafe(m.getCashFlow()));
            totalTransactions += nullSafe(m.getTransactionCount());
        }

        int months = summaries.size();

        return FinancialAggregate.builder()
                .avgIncome(divide(totalIncome, months))
                .avgExpense(divide(totalExpense, months))
                .avgCashFlow(divide(totalCashFlow, months))
                .avgTransactionAmount(
                        totalTransactions == 0
                                ? BigDecimal.ZERO
                                : totalIncome.add(totalExpense)
                                .divide(BigDecimal.valueOf(totalTransactions), 2, RoundingMode.HALF_UP)
                )
                .monthlyTransactionCount((int) (totalTransactions / months))
                .expenseGrowth(growthPercent(
                        nullSafe(summaries.get(months - 2).getTotalExpense()),
                        nullSafe(summaries.get(months - 1).getTotalExpense())
                ))
                .incomeGrowth(growthPercent(
                        nullSafe(summaries.get(months - 2).getTotalIncome()),
                        nullSafe(summaries.get(months - 1).getTotalIncome())
                ))
                .fromMonth(fromMonth)
                .toMonth(currentMonth)
                .build();
    }

    private List<AnalyticsCategoryResponse> buildCategoryStats(Long userId, LocalDate from, LocalDate to) {
        List<AnalyticsCategorySummary> summaries =
                categorySummaryRepo.findAllByIdUserIdAndIdDateBetween(userId, from, to);

        if (summaries.isEmpty()) return List.of();

        BigDecimal total = summaries.stream()
                .map(AnalyticsCategorySummary::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (total.compareTo(BigDecimal.ZERO) == 0) return List.of();

        return summaries.stream()
                .collect(Collectors.groupingBy(
                        s -> s.getId().getCategory(),
                        Collectors.reducing(BigDecimal.ZERO,
                                AnalyticsCategorySummary::getTotalAmount,
                                BigDecimal::add)
                ))
                .entrySet()
                .stream()
                .map(e -> {
                    BigDecimal amount = e.getValue();
                    int percent = amount.multiply(BigDecimal.valueOf(100))
                            .divide(total, 0, RoundingMode.HALF_UP)
                            .intValue();

                    return new AnalyticsCategoryResponse(
                            e.getKey(),
                            amount,
                            percent
                    );
                })
                .sorted(Comparator.comparing(AnalyticsCategoryResponse::totalAmount).reversed())
                .toList();
    }

}