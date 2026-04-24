import {Link} from "react-router-dom";
import type {Card} from "@/domain/card/card.types.ts";
import type {Account} from "@/domain/account/account.types.ts";
import styles from "./CardInfoBlock.module.css"
import {StateMessage} from "@/presentation/ui/state_message/StateMessage.tsx";

interface Props{
    card: Card | null
    account: Account | null
    loading: boolean
}

export function CardInfoBlock({card,account,loading }: Props) {
    if (loading)
        return <StateMessage>Завантаження...</StateMessage>

    if (!account)
        return <StateMessage>У вас немає рахунків</StateMessage>

    if (!card)
        return <StateMessage>Картку не знайдено</StateMessage>

    return (
        <section className={styles.container}>

            <div className={styles.header}>
                <h3 className={styles.title}>Моя картка</h3>

                <Link to="/accounts" className={styles.link}>
                    Усі рахунки
                </Link>
            </div>

            <div className={styles.card}>
                <div>{card.cardType}</div>

                <div className={styles.cardNumber}>
                    {card.cardNumber}
                </div>

                <div className={styles.cardBottom}>
                    <div>
                        <div>{card.expirationDate}</div>
                    </div>

                    <div className={styles.network}>
                        {card.cardNetwork}
                    </div>
                </div>
            </div>

            <div className={styles.accountInfo}>
                <div>Баланс</div>

                <div className={styles.balance}>
                    {account.balance} {account.currency}
                </div>
            </div>

        </section>
    )
}