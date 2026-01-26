package com.lumina_bank.paymentservice.model;

import com.lumina_bank.common.enums.payment.Currency;
import com.lumina_bank.paymentservice.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    private Long transactionId;

    @Column(nullable = false)
    private String fromCardNumber;
    @Column(nullable = false)
    private String toCardNumber;

    private Long fromAccountId;
    private Long toAccountId;

    @Enumerated(EnumType.STRING)
    private Currency fromCurrency;
    @Enumerated(EnumType.STRING)
    private Currency toCurrency;

    @Column(precision = 15, scale = 6)
    private BigDecimal exchangeRate;

    @Column(precision = 15, scale = 6)
    private BigDecimal convertedAmount;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id")
    private PaymentTemplate template;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime completedAt;
}