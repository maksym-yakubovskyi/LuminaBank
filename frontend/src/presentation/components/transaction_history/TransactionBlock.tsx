import type {TransactionHistoryItem} from "@/domain/transaction/transaction.types.ts";
import styles from "./TransactionBlock.module.css"
import { formatAmount, formatDate } from "@/shared/utils/helpers"
import {StateMessage} from "@/presentation/ui/state_message/StateMessage.tsx";

interface Props {
    transaction: TransactionHistoryItem | null
}

export default function TransactionBlock({transaction}: Props) {
    if (!transaction)
        return <StateMessage>Оберіть транзакцію</StateMessage>

    const isIncoming = transaction.direction === "INCOMING"
    const isOutgoing = transaction.direction === "OUTGOING"
    const isInternal = transaction.direction === "INTERNAL"

    const statusClass = (() => {
        switch (transaction.status) {
            case "SUCCESS":
                return styles.success
            case "FAILED":
            case "REJECTED":
            case "BLOCKED":
                return styles.failed
            case "PENDING":
            case "PROCESSING":
            case "RISK_PENDING":
                return styles.pending
            default:
                return styles.defaultStatus
        }
    })()

    return (
        <section className={styles.container}>

            {/* HEADER */}
            <div className={styles.header}>

                <div
                    className={`
                        ${styles.amount}
                        ${isIncoming ? styles.incoming : ""}
                        ${isOutgoing ? styles.outgoing : ""}
                        ${isInternal ? styles.internal : ""}
                    `}
                >
                    {isIncoming ? "+" : isOutgoing ? "-" : ""}
                    {formatAmount(transaction.amount, transaction.currency)}
                </div>

                <div className={`${styles.status} ${statusClass}`}>
                    {transaction.status}
                </div>

            </div>

            {/* DETAILS */}
            <div className={styles.details}>

                <div className={styles.label}>Дата</div>
                <div className={styles.value}>
                    {formatDate(transaction.date)}
                </div>

                <div className={styles.label}>Напрям</div>
                <div className={styles.value}>
                    {transaction.direction}
                </div>

                <div className={styles.label}>ID транзакції</div>
                <div className={styles.value}>
                    #{transaction.paymentId}
                </div>

                {transaction.description && (
                    <>
                        <div className={styles.label}>Опис</div>
                        <div className={styles.value}>
                            {transaction.description}
                        </div>
                    </>
                )}

            </div>

        </section>
    )
}