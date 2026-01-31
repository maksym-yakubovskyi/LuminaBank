package com.lumina_bank.analyticsservice.model;

import com.lumina_bank.analyticsservice.model.embedded.CategorySummaryId;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;

@Entity
@Table(name = "analytics_category_summary")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalyticsCategorySummary {
    @EmbeddedId
    private CategorySummaryId id;

    private BigDecimal totalAmount;
    private Long transactionCount;
}
