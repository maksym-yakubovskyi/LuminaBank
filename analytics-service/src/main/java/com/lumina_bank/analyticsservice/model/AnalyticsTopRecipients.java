package com.lumina_bank.analyticsservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "analytics_top_recipients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalyticsTopRecipients {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long recipientId;

    private BigDecimal totalAmount;
    private Long transactionCount;

    private LocalDateTime lastTransactionAt;
}
