package com.lumina_bank.transactionservice.model;

import com.lumina_bank.common.enums.payment.Currency;
import com.lumina_bank.transactionservice.enums.TransactionStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    @Column(nullable = false)
    private Long fromAccountId;
    @Column(nullable = false)
    private Long toAccountId;
    @Column(nullable = false,precision = 15, scale = 2)
    private BigDecimal amount;
    private String description;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionStatus transactionStatus;

    @Enumerated(EnumType.STRING)
    private Currency fromCurrency;
    @Enumerated(EnumType.STRING)
    private Currency toCurrency;

    @Column(precision = 15, scale = 6)
    private BigDecimal exchangeRate;

    @Column(precision = 15, scale = 6)
    private BigDecimal convertedAmount;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
