package com.lumina_bank.accountservice.model;

import com.lumina_bank.accountservice.enums.LoanStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "loans")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account creditAccount;

    /**
     * Початкова сума кредиту (тіло кредиту),
     * яку банк видав клієнту.
     */
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal principalAmount;

    /**
     * Річна відсоткова ставка у відсотках.
     * Наприклад: 12.50 означає 12.5% річних.
     */
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal interestRate;

    /**
     * Термін кредиту в місяцях.
     * Наприклад: 24 означає 2 роки.
     */
    @Column(nullable = false)
    private Integer termMonths;

    /**
     * Щомісячний платіж (ануїтет),
     * який включає частину тіла та частину відсотків.
     */
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal monthlyPayment;

    /**
     * Поточний залишок тіла кредиту,
     * який ще потрібно погасити.
     * Зменшується після кожної оплати.
     */
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal remainingPrincipal;

    /**
     * Загальна сума відсотків,
     * яку клієнт сплатить за весь термін кредиту.
     */
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal totalInterestAmount;

    /**
     * Загальна сума до сплати:
     * principalAmount + totalInterestAmount.
     */
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal totalPayableAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanStatus status;

    private Integer riskScore;

    private LocalDateTime approvedAt;
    private LocalDateTime closedAt;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Графік платежів по кредиту.
     * Один кредит має багато щомісячних внесків.
     */
    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL)
    private List<LoanInstallment> installments;
}

