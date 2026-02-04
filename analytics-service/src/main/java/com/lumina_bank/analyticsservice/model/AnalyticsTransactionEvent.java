package com.lumina_bank.analyticsservice.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "analytics_transaction_event")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalyticsTransactionEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long paymentId;
    private Long userId;
    private Long accountId;

    private String direction;
    private String category;

    private BigDecimal amount;
    private String currency;

    private Integer riskScore;
    private String status;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    private Instant processedAt;
}
