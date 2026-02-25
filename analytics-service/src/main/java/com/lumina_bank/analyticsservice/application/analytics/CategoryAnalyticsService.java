package com.lumina_bank.analyticsservice.application.analytics;

import com.lumina_bank.analyticsservice.api.response.analytics.AnalyticsCategoryResponse;
import com.lumina_bank.analyticsservice.domain.model.AnalyticsCategorySummary;
import com.lumina_bank.analyticsservice.domain.repository.AnalyticsCategorySummaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryAnalyticsService {
    private final AnalyticsCategorySummaryRepository categoryRepo;

    @Transactional(readOnly = true)
    public List<AnalyticsCategoryResponse> getTopCategoriesForPeriod(
            Long userId,
            YearMonth fromMonth,
            YearMonth toMonth
    ) {
        return buildCategoryStats(userId, fromMonth.atDay(1), toMonth.atEndOfMonth());
    }

    public List<AnalyticsCategoryResponse> buildCategoryStats(Long userId, LocalDate from, LocalDate to) {
        List<AnalyticsCategorySummary> summaries =
                categoryRepo.findAllByIdUserIdAndIdDateBetween(userId, from, to);

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
