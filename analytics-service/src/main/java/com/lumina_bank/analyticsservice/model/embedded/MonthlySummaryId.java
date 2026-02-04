package com.lumina_bank.analyticsservice.model.embedded;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.time.YearMonth;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class MonthlySummaryId {
    private YearMonth yearMonth;
    private Long userId;
    private Long accountId;
}
