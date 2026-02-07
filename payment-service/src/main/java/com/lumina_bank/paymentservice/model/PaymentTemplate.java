package com.lumina_bank.paymentservice.model;

import com.lumina_bank.paymentservice.enums.PaymentTemplateType;
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentTemplateType type;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private String fromCardNumber;

    @Column(nullable = false)
    private String toCardNumber;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private Boolean isRecurring;

    private String recurrenceCron;

    private LocalDateTime nextExecutionTime;

    @Column(nullable = false)
    private Boolean isActive;
}