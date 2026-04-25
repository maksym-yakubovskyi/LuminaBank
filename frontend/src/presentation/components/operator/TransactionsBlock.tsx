import type {TransactionHistoryItem} from "@/domain/transaction/transaction.types.ts";
import type {PaymentStatus} from "@/domain/payment/payment-status-enum.ts";
import {StateMessage} from "@/presentation/ui/state_message/StateMessage.tsx";
import {Button} from "@/presentation/ui/button/Button.tsx";
import AdminPaymentService from "@/application/admin/AdminPaymentService.ts";
import styles from "./TransactionsBlock.module.css"

interface Props {
    transactions: TransactionHistoryItem[] | null
    loading: boolean
    currentFilter: PaymentStatus | "ALL"
    onFilterChange: (status: PaymentStatus | "ALL") => void
}

export function TransactionsBlock({
                                      transactions,
                                      loading,
                                      currentFilter,
                                      onFilterChange
                                  }: Props) {

    if (loading)
        return <StateMessage>Завантаження транзакцій...</StateMessage>

    if (!transactions)
        return <StateMessage>Оберіть рахунок</StateMessage>

    if (transactions.length === 0)
        return <StateMessage>Транзакцій не знайдено</StateMessage>

    async function handleApprove(id: number) {
        await AdminPaymentService.approve(id)
        onFilterChange(currentFilter)
    }

    async function handleReject(id: number) {
        await AdminPaymentService.reject(id)
        onFilterChange(currentFilter)
    }

    return (
        <section className={styles.container}>
            <h3 className={styles.title}>Транзакції</h3>

            <div className={styles.filters}>

                {["ALL","SUCCESS","FAILED","PENDING"].map(status => (
                    <Button
                        key={status}
                        onClick={() => onFilterChange(status as any)}
                    >
                        {status}
                    </Button>
                ))}
            </div>

            <div className={styles.list}>
                {transactions.map(t => (
                    <div key={t.paymentId} className={styles.card}>

                        <div className={styles.row}>
                            <span className={styles.label}>ID</span>
                            <span className={styles.value}>{t.paymentId}</span>
                        </div>

                        <div className={styles.row}>
                            <span className={styles.label}>Сума</span>
                            <span className={styles.value}>
                        {t.amount} {t.currency}
                    </span>
                        </div>

                        <div className={`${styles.status} ${
                            t.status === "SUCCESS" ? styles.success :
                                t.status === "FAILED" || t.status === "REJECTED" ? styles.failed :
                                    styles.pending
                        }`}>
                            {t.status}
                        </div>

                        {t.status === "FLAGGED" && (
                            <div className={styles.actions}>
                                <Button onClick={() => handleApprove(t.paymentId)}>
                                    Approve
                                </Button>

                                <Button variant="danger"
                                        onClick={() => handleReject(t.paymentId)}>
                                    Reject
                                </Button>
                            </div>
                        )}

                    </div>
                ))}
            </div>
        </section>
    )
}