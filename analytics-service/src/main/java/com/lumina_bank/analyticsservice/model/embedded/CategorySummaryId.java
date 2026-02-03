package com.lumina_bank.analyticsservice.model.embedded;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.time.LocalDate;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class CategorySummaryId {
    private LocalDate date;
    private Long userId;
    private String category;
}
