package com.lumina_bank.paymentservice.model;

import com.lumina_bank.paymentservice.enums.PaymentType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_templates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Long fromAccountId;
    @Column(nullable = false)
    private Long toAccountId;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentType paymentType;

    @Column(nullable = false)
    private Boolean isRecurring;
    private String recurrenceCron;

    private LocalDateTime nextExecutionTime;

    @Column(nullable = false)
    private Boolean isActive;
}
