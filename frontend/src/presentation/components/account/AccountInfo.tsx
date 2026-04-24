import type {Card} from "@/domain/card/card.types.ts";
import type {Account} from "@/domain/account/account.types.ts";
import styles from "./AccountInfo.module.css"
import {StateMessage} from "@/presentation/ui/state_message/StateMessage.tsx";
import  {Status} from "@/domain/shared/status.enum.ts";
import {formatAmount} from "@/shared/utils/helpers.ts";

interface Props {
    card: Card | null
    account: Account | null
}

export default function AccountInfo({card , account}: Props) {
    if (!card || !account) {
        return <StateMessage>Виберіть рахунок...</StateMessage>
    }

    function getStatusClass(status: Status) {
        switch (status) {
            case Status.ACTIVE:
                return styles.active
            case Status.BLOCKED:
                return styles.blocked
            default:
                return styles.inactive
        }
    }

    return (
        <section className={styles.container}>

            <div className={styles.section}>

                <h4 className={styles.sectionTitle}>
                    Картка
                </h4>

                <div className={styles.grid}>
                    <div className={styles.label}>Тип картки</div>
                    <div className={styles.value}>{card.cardType}</div>

                    <div className={styles.label}>Платіжна система</div>
                    <div className={styles.value}>{card.cardNetwork}</div>

                    <div className={styles.label}>Номер</div>
                    <div className={styles.value}>
                        {(card.cardNumber)}
                    </div>

                    <div className={styles.label}>Термін дії</div>
                    <div className={styles.value}>
                        {card.expirationDate}
                    </div>

                    <div className={styles.label}>Статус</div>
                    <div className={styles.value}>
                        <span
                            className={`
                                ${styles.status}
                                ${getStatusClass(card.status)}
                            `}
                        >
                            {card.status}
                        </span>
                    </div>

                    <div className={styles.label}>Ліміт</div>
                    <div className={styles.value}>
                        {formatAmount(card.limit, account.currency)}
                    </div>
                </div>
            </div>

            <div className={styles.section}>

                <h4 className={styles.sectionTitle}>
                    Рахунок
                </h4>

                <div className={styles.grid}>
                    <div className={styles.label}>Баланс</div>
                    <div className={`${styles.value} ${styles.balance}`}>
                        {formatAmount(account.balance, account.currency)}
                    </div>

                    <div className={styles.label}>IBAN</div>
                    <div className={styles.value}>
                        {account.iban}
                    </div>

                    <div className={styles.label}>Тип рахунку</div>
                    <div className={styles.value}>
                        {account.type}
                    </div>

                    <div className={styles.label}>Статус</div>
                    <div className={styles.value}>
                        <span
                            className={`
                                ${styles.status}
                                ${getStatusClass(account.status)}
                            `}
                        >
                            {account.status}
                        </span>
                    </div>
                </div>
            </div>

        </section>
    )
}
