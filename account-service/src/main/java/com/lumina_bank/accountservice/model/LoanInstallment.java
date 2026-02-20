package com.lumina_bank.accountservice.model;
import com.lumina_bank.accountservice.enums.InstallmentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "loan_installments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanInstallment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;

    /**
     * Порядковий номер платежу у графіку.
     * Наприклад: 1, 2, 3 ... N.
     */
    private Integer installmentNumber;

    /**
     * Дата, до якої клієнт повинен здійснити оплату.
     */
    private LocalDate dueDate;

    /**
     * Частина платежу, яка погашає тіло кредиту.
     */
    @Column(precision = 15, scale = 2)
    private BigDecimal principalPart;

    /**
     * Частина платежу, яка є відсотками
     * (дохід банку).
     */
    @Column(precision = 15, scale = 2)
    private BigDecimal interestPart;

    /**
     * Загальна сума платежу:
     * principalPart + interestPart.
     */
    @Column(precision = 15, scale = 2)
    private BigDecimal totalAmount;

    /**
     * Скільки фактично сплачено по цьому платежу.
     * Може бути менше totalAmount при частковій оплаті.
     */
    @Column(precision = 15, scale = 2)
    private BigDecimal paidAmount;

    @Column(precision = 15, scale = 2)
    private BigDecimal penaltyAmount;

    /**
     * Дата фактичної оплати.
     * Null, якщо ще не оплачено.
     */
    private LocalDate paidAt;

    @Enumerated(EnumType.STRING)
    private InstallmentStatus status;
}