package com.lumina_bank.analyticsservice.repository;

import com.lumina_bank.analyticsservice.model.AnalyticsMonthlySummary;
import com.lumina_bank.analyticsservice.model.embedded.MonthlySummaryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnalyticsMonthlySummaryRepository extends JpaRepository<AnalyticsMonthlySummary, MonthlySummaryId> {
}
