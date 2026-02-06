package com.lumina_bank.analyticsservice.repository;

import com.lumina_bank.analyticsservice.model.AnalyticsDailySummary;
import com.lumina_bank.analyticsservice.model.embedded.DailySummaryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AnalyticsDailySummaryRepository extends JpaRepository<AnalyticsDailySummary, DailySummaryId> {
    List<AnalyticsDailySummary> findAllByIdUserIdAndIdDateBetween(
            Long userId,
            LocalDate from,
            LocalDate to
    );

}
