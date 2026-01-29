package com.lumina_bank.frauddetectionservice.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_fraud_profiles")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserFraudProfile {
    @Id
    private Long id;

    @Column(nullable = false)
    private Integer totalPayments;

    @Column(nullable = false)
    private BigDecimal avgAmount30d;

    @Column(nullable = false)
    private LocalDateTime firstPaymentDate;
    @Column(nullable = false)
    private LocalDateTime lastPaymentDate;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private Instant updatedAt;

    public void registerNewPayment(BigDecimal amount, LocalDateTime occurredAt) {

        if (totalPayments == null) {
            totalPayments = 0;
        }

        totalPayments++;

        if (firstPaymentDate == null) {
            firstPaymentDate = occurredAt;
        }

        lastPaymentDate = occurredAt;

        recalculateAvgAmount30d(amount);
    }

    private void recalculateAvgAmount30d(BigDecimal newAmount) {

        if (avgAmount30d == null) {
            avgAmount30d = newAmount;
            return;
        }

        /*
         * Просте ковзне середнє (EMA-подібне)
         * Не потребує зберігання всієї історії
         */
        BigDecimal weight = BigDecimal.valueOf(0.8);

        avgAmount30d = avgAmount30d
                .multiply(weight)
                .add(newAmount.multiply(BigDecimal.ONE.subtract(weight)));
    }
    public boolean isFirstPayment() {
        return totalPayments != null && totalPayments == 1;
    }
}