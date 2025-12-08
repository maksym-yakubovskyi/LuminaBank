package com.lumina_bank.accountservice.model;

import com.lumina_bank.accountservice.enums.CardNetwork;
import com.lumina_bank.accountservice.enums.CardType;
import com.lumina_bank.accountservice.enums.Status;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;

@Entity
@Table(name = "cards")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, unique = true, length = 16)
    private String cardNumber;
    @Column(nullable = false)
    private YearMonth expirationDate;
    @Column(nullable = false, length = 3)
    private String cvv;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CardType cardType;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CardNetwork cardNetwork;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;
    @Column(nullable = false, name = "card_limit",precision = 15, scale = 2)
    private BigDecimal limit;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;
}
