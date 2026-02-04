package com.lumina_bank.transactionservice.model;

import com.lumina_bank.common.enums.payment.Currency;
import com.lumina_bank.common.enums.payment.PaymentDirection;
import com.lumina_bank.transactionservice.enums.TransactionStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transactions")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long userId;

    @Column(nullable = false)
    private String cardNumber;
    @Enumerated(EnumType.STRING)
    private Currency currency;
    @Column(precision = 15, scale = 6)
    private BigDecimal exchangeRate;
    @Column(nullable = false,precision = 15, scale = 2)
    private BigDecimal amount;

    private String category;
    private String description;

    @Column(nullable = false)
    private UUID transferId;
    private PaymentDirection direction;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionStatus transactionStatus;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}