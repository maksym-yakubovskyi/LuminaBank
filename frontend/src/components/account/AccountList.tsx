import type {Card} from "@/features/types/card.ts";
import {Link} from "react-router-dom";
import {Status} from "@/features/enum/enum.ts";

interface Props {
    cards: Card[] | null
    loading: boolean
}

export default function AccountList({ cards, loading }: Props) {

    if (loading) return <>Завантаження...</>
    if (!cards || cards.length === 0) return <>Список карток порожній</>

    function maskCardNumber(cardNumber: string) {
        if (cardNumber.length < 4) return cardNumber
        return `**** **** **** ${cardNumber.slice(-4)}`
    }

    return (
        <>
            <h3>Усі картки</h3>
            <ul style={{ listStyle: "none", padding: 0 }}>
                {cards.map(card => (
                    <li key={card.id}>
                        <Link
                            to={`/accounts/${card.id}`}
                            style={{
                                display: "block",
                                padding: "12px 0",
                                borderBottom: "1px solid #eee",
                                textDecoration: "none",
                                color: "inherit",
                                cursor: "pointer",
                            }}
                        >
                            <div
                                style={{
                                    display: "flex",
                                    justifyContent: "space-between",
                                    alignItems: "center",
                                }}
                            >
                                <div>
                                    <strong>{card.cardType}</strong>
                                    <div style={{ fontSize: "0.85em", color: "#555" }}>
                                        {card.cardNetwork} · {maskCardNumber(card.cardNumber)}
                                    </div>
                                </div>

                                <div
                                    style={{
                                        fontSize: "0.8em",
                                        color:
                                            card.status === Status.ACTIVE
                                                ? "green"
                                                : "gray",
                                    }}
                                >
                                    {card.status}
                                </div>
                            </div>
                        </Link>
                    </li>
                ))}
            </ul>
        </>
    )
}