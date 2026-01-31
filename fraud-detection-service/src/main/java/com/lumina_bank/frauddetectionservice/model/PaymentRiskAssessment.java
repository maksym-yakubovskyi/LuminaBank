package com.lumina_bank.frauddetectionservice.model;

import com.lumina_bank.common.enums.payment.RiskLevel;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment_risk_assessment")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRiskAssessment {
    @Id
    private Long id;

    @Column(nullable = false)
    private Integer riskScore;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RiskLevel riskLevel;

    @Column(nullable = false)
    private String reasons;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime assessmentAt;
}
