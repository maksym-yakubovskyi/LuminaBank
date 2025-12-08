package com.lumina_bank.paymentservice.model;

import com.lumina_bank.paymentservice.enums.Currency;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "exchange_rates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExchangeRate {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Currency baseCurrency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Currency targetCurrency;

    @Column(nullable = false, precision = 15, scale = 6)
    private BigDecimal rate;

    @Column(nullable = false)
    private LocalDateTime date;
}
