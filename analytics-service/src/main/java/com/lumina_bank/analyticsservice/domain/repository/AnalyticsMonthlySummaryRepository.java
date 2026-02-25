package com.lumina_bank.analyticsservice.domain.repository;

import com.lumina_bank.analyticsservice.domain.model.AnalyticsMonthlySummary;
import com.lumina_bank.analyticsservice.domain.model.embedded.MonthlySummaryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.YearMonth;
import java.util.List;

@Repository
public interface AnalyticsMonthlySummaryRepository extends JpaRepository<AnalyticsMonthlySummary, MonthlySummaryId> {
    List<AnalyticsMonthlySummary> findByIdUserIdAndIdYearMonthBetween(Long userId, YearMonth fromMonth, YearMonth currentMonth);
}
