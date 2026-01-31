package com.lumina_bank.analyticsservice.repository;

import com.lumina_bank.analyticsservice.model.AnalyticsCategorySummary;
import com.lumina_bank.analyticsservice.model.embedded.CategorySummaryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnalyticsCategorySummaryRepository extends JpaRepository<AnalyticsCategorySummary, CategorySummaryId> {
}
