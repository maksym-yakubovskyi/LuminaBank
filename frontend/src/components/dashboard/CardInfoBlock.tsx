import {Link} from "react-router-dom";
import type {Card} from "@/features/types/card.ts";
import type {Account} from "@/features/types/account.ts";

interface Props{
    card: Card | null
    account: Account | null
}

export function CardInfoBlock({card,account}: Props) {
    if (!card || !account) {
        return <>Завантаження...</>
    }
    return (<>
            <Link
                to="/accounts"
                style={{
                    display: "inline-block",
                    marginTop: "16px",
                    textDecoration: "none",
                    padding: "8px 12px",
                    border: "1px solid #000",
                    borderRadius: "4px",
                }}
            >
                Усі рахунки
            </Link>
            <section
                style={{
                    border: "1px solid #ddd",
                    borderRadius: "8px",
                    padding: "16px",
                }}>

                <h3>Моя картка</h3>

                <div style={{marginTop: "12px"}}>
                    <p>{card.cardType}</p>
                    <p><strong>Номер:</strong> {card.cardNumber}</p>
                    <p><strong>Баланс:</strong> {account.balance}</p>
                    <p>{card.expirationDate} </p>
                    <strong>{card.cardNetwork}</strong>
                </div>
            </section>
        </>
    )
}