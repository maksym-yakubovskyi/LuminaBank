package com.lumina_bank.analyticsservice.repository;

import com.lumina_bank.analyticsservice.model.AnalyticsDailySummary;
import com.lumina_bank.analyticsservice.model.embedded.DailySummaryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnalyticsDailySummaryRepository extends JpaRepository<AnalyticsDailySummary, DailySummaryId> {
}
