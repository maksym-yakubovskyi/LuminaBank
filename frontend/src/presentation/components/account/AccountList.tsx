import type {Card} from "@/domain/card/card.types.ts";
import {Link} from "react-router-dom";
import {Status} from "@/domain/shared/status.enum.ts";
import styles from "./AccountList.module.css"
import {StateMessage} from "@/presentation/ui/state_message/StateMessage.tsx";

interface Props {
    cards: Card[] | null
    loading: boolean
}

export default function AccountList({ cards, loading }: Props) {

    if (loading)
        return <StateMessage>Завантаження...</StateMessage>

    if (!cards || cards.length === 0)
        return <StateMessage>Список карток порожній</StateMessage>

    function maskCardNumber(cardNumber: string) {
        if (cardNumber.length < 4) return cardNumber
        return `**** **** **** ${cardNumber.slice(-4)}`
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
       <>
           <h3 className={styles.title}>Усі картки</h3>

           <ul className={styles.list}>
               {cards.map(card => (
                   <li key={card.id}>
                       <Link
                           to={`/accounts/${card.id}`}
                           className={styles.item}
                       >
                           <div className={styles.left}>
                               <div className={styles.cardType}>
                                   {card.cardType}
                               </div>

                               <div className={styles.cardMeta}>
                                   {card.cardNetwork} · {maskCardNumber(card.cardNumber)}
                               </div>
                           </div>

                           <div className={styles.right}>
                                <span
                                    className={`${styles.status} ${getStatusClass(card.status)}`}
                                >
                                    {card.status}
                                </span>
                           </div>
                       </Link>
                   </li>
               ))}
           </ul>
       </>
    )
}