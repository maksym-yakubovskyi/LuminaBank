import {StateMessage} from "@/presentation/ui/state_message/StateMessage.tsx";
import type {Account} from "@/domain/account/account.types.ts";
import styles from "./AccountList.module.css"

interface Props {
    accounts: Account[] | null
    loading: boolean
    onSelect: (accountId: number) => void
}

export function AccountList({ accounts, loading, onSelect }: Props) {

    if (loading)
        return <StateMessage>Завантаження рахунків...</StateMessage>

    if (!accounts || accounts.length === 0)
        return <StateMessage>Рахунків немає</StateMessage>

    return (
        <section className={styles.container}>
            <h3 className={styles.title}>Рахунки користувача</h3>

            <div className={styles.list}>
                {accounts.map(acc => (
                    <div
                        key={acc.id}
                        onClick={() => onSelect(acc.id)}
                        className={styles.card}
                    >
                        <div className={styles.row}>
                            <span className={styles.label}>IBAN</span>
                            <span className={styles.value}>{acc.iban}</span>
                        </div>

                        <div className={styles.row}>
                            <span className={styles.label}>Баланс</span>
                            <span className={styles.value}>
                        {acc.balance} {acc.currency}
                    </span>
                        </div>

                        <div className={`${styles.status} ${
                            acc.status === "ACTIVE" ? styles.active : styles.blocked
                        }`}>
                            {acc.status}
                        </div>
                    </div>
                ))}
            </div>
        </section>
    )
}