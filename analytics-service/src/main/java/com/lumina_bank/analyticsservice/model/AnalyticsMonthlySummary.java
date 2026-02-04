package com.lumina_bank.analyticsservice.model;

import com.lumina_bank.analyticsservice.model.embedded.MonthlySummaryId;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "analytics_monthly_summary")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalyticsMonthlySummary {
    @EmbeddedId
    private MonthlySummaryId id;

    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal cashFlow;

    private Long transactionCount;
}
