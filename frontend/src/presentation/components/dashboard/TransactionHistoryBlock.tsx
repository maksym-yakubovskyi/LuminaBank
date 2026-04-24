import type {TransactionHistoryItem} from "@/domain/transaction/transaction.types.ts";
import {Link} from "react-router-dom";
import styles from "./TransactionHistoryBlock.module.css"
import {formatAmount, formatDate} from "@/shared/utils/helpers.ts";
import {StateMessage} from "@/presentation/ui/state_message/StateMessage.tsx";

interface Props {
    history: TransactionHistoryItem[] | null
    loading: boolean
}

export function TransactionHistoryBlock({ history,loading }: Props) {

    if (loading)
        return <StateMessage>Завантаження...</StateMessage>

    if (!history || history.length === 0)
        return <StateMessage>Історія транзакцій порожня</StateMessage>

    return (
        <section className={styles.container}>

            <div className={styles.header}>
                <h3 className={styles.title}>Історія</h3>
            </div>

            <ul className={styles.list}>
                {history.map(item => {

                    const incoming =
                        item.direction === "INCOMING"

                    return (
                        <li key={item.paymentId}>
                            <Link
                                to={`/transactions/${item.paymentId}`}
                                className={styles.item}
                            >
                                <div className={styles.left}>
                                    <div className={styles.description}>
                                        {item.description ?? "Платіж"}
                                    </div>

                                    <div className={styles.date}>
                                        {formatDate(item.date)}
                                    </div>
                                </div>

                                <div className={styles.right}>
                                    <div
                                        className={`${styles.amount}
                                        ${incoming
                                            ? styles.incoming
                                            : styles.outgoing}`}
                                    >
                                        {incoming ? "+" : "-"}
                                        {formatAmount(
                                            item.amount,
                                            item.currency
                                        )}
                                    </div>

                                    <div className={styles.status}>
                                        {item.status}
                                    </div>
                                </div>
                            </Link>
                        </li>
                    )
                })}
            </ul>

        </section>
    )
}