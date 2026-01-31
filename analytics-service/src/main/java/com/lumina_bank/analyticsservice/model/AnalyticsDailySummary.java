package com.lumina_bank.analyticsservice.model;

import com.lumina_bank.analyticsservice.model.embedded.DailySummaryId;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.math.BigDecimal;

@Entity
@Table(name = "analytics_daily_summary")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalyticsDailySummary {

    @EmbeddedId
    private DailySummaryId id;

    private BigDecimal totalIncome;
    private BigDecimal totalExpense;

    private Long transactionCount;
    private Long flaggedCount;
}
