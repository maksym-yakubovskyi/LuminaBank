import type {TransactionHistoryItem} from "@/features/types/transactionHistoryItem.ts";
import {Link} from "react-router-dom";

interface Props {
    history: TransactionHistoryItem[] | null
}

export function TransactionHistoryBlock({ history }: Props) {

    if (!history) return <>Завантаження...</>
    if (history.length === 0) return <>Історія транзакцій порожня</>

    return (
        <>
            <h3>Історія</h3>
            <ul style={{ listStyle: "none", padding: 0 }}>
                {history.map(item => (
                    <li key={item.paymentId}>
                        <Link
                            to={`/transactions/${item.paymentId}`}
                            style={{
                                display: "block",
                                padding: "8px 0",
                                borderBottom: "1px solid #eee",
                                textDecoration: "none",
                                color: "inherit",
                            }}
                        >
                            <div style={{ display: "flex", justifyContent: "space-between" }}>
                                <div>
                                    <div style={{ fontSize: "0.9em", color: "#555" }}>
                                        {item.date}
                                    </div>
                                </div>
                                <div>
                                    <span
                                        style={{
                                            color:
                                                item.direction === "INCOMING"
                                                    ? "green"
                                                    : "red",
                                            fontWeight: "bold",
                                        }}
                                    >
                                        {item.direction === "INCOMING" ? "+" : "-"}
                                        {item.amount} {item.currency}
                                    </span>
                                    <div style={{ fontSize: "0.8em", color: "#999" }}>
                                        {item.status}
                                    </div>
                                </div>
                            </div>
                        </Link>
                    </li>
                ))}
            </ul>
        </>
    )
}