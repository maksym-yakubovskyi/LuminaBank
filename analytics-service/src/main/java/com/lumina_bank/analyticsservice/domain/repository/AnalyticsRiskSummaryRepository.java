package com.lumina_bank.analyticsservice.domain.repository;

import com.lumina_bank.analyticsservice.domain.model.AnalyticsRiskSummary;
import com.lumina_bank.analyticsservice.domain.model.embedded.RiskSummaryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnalyticsRiskSummaryRepository extends JpaRepository<AnalyticsRiskSummary, RiskSummaryId> {
}
