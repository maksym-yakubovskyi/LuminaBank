package com.lumina_bank.analyticsservice.model.embedded;

import jakarta.persistence.Embeddable;
import java.time.LocalDate;
import lombok.*;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class RiskSummaryId {
    private LocalDate date;
    private Long userId;
}

