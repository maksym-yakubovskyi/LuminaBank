import type {LoanResponse} from "@/domain/loan/loan.types.ts";
import  {Currency} from "@/domain/shared/currency.enum.ts";
import styles from "./LoanDetails.module.css"
import {formatAmount, formatDate} from "@/shared/utils/helpers.ts";
import {InstallmentStatus, LoanStatus} from "@/domain/loan/loan-status.enum.ts";

interface Props {
    loan: LoanResponse
    currency:  Currency | undefined
}

export default function LoanDetails({ loan, currency }: Props) {

    const statusClassMap: Record<InstallmentStatus, string> = {
        PENDING: styles.pending,
        DUE: styles.due,
        PARTIALLY_PAID: styles.partial,
        PAID: styles.paid,
        OVERDUE: styles.overdue,
        CLOSED: styles.closed,
    }

    return (
        <div className={styles.container}>

            <h3 className={styles.title}>Деталі кредиту</h3>

            {/* SUMMARY */}
            <div className={styles.summaryCard}>

                <div className={styles.kpi}>
                    <div className={styles.kpiLabel}>Сума кредиту</div>
                    <div className={styles.kpiValue}>
                        {formatAmount(loan.principalAmount, (currency || Currency.UAH))}
                    </div>
                </div>

                <div className={styles.kpi}>
                    <div className={styles.kpiLabel}>Ставка</div>
                    <div className={styles.kpiValue}>
                        {loan.interestRate}%
                    </div>
                </div>

                <div className={styles.kpi}>
                    <div className={styles.kpiLabel}>Щомісячний платіж</div>
                    <div className={styles.kpiValue}>
                        {formatAmount(loan.monthlyPayment, (currency || Currency.UAH))}
                    </div>
                </div>

                <div className={styles.kpi}>
                    <div className={styles.kpiLabel}>Залишок</div>
                    <div className={styles.kpiValue}>
                        {formatAmount(loan.remainingPrincipal, (currency || Currency.UAH))}
                    </div>
                </div>

                <div className={styles.kpi}>
                    <div className={styles.kpiLabel}>Статус</div>
                    <div className={`
                        ${styles.kpiValue}
                        ${styles.status}
                        ${
                        loan.status === LoanStatus.ACTIVE
                            ? styles.statusActive
                            : loan.status === LoanStatus.CLOSED
                                ? styles.statusClosed
                                : styles.statusOverdue
                    }
                    `}>
                        {loan.status}
                    </div>
                </div>

            </div>

            {/* INSTALLMENTS */}
            <div className={styles.installmentsSection}>

                <div className={styles.installmentsTitle}>
                    Графік платежів
                </div>

                {loan.installments.map(inst => (
                    <div key={inst.id} className={styles.installmentRow}>

                        <div className={styles.installmentNumber}>
                            #{inst.installmentNumber}
                        </div>

                        <div>
                            {formatDate(inst.dueDate)}
                        </div>

                        <div>
                            {formatAmount(inst.totalAmount, (currency || Currency.UAH))}
                        </div>

                        <div className={`
                            ${styles.installmentStatus}
                            ${statusClassMap[inst.status]}
                        `}>
                            {inst.status}
                        </div>

                    </div>
                ))}

            </div>

        </div>
    )
}
