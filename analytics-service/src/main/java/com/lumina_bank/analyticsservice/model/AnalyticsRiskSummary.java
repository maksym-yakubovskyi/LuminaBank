package com.lumina_bank.analyticsservice.model;

import com.lumina_bank.analyticsservice.model.embedded.RiskSummaryId;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "analytics_risk_summary")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalyticsRiskSummary {

    @EmbeddedId
    private RiskSummaryId id;

    private Integer avgRiskScore;
    private Integer maxRiskScore;
    private Integer minRiskScore;

    private Integer flaggedCount;
}
