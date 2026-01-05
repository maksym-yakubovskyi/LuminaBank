import type {TransactionHistoryItem} from "@/features/types/transactionHistoryItem.ts";

interface Props {
    history: TransactionHistoryItem[] | null
}
export function TransactionHistoryBlock({history}:Props){
    if (!history) return <>Завантаження...</>
    if (history.length === 0) return <>Історія транзакцій порожня</>


    return (
        <>
            <h3>Історія</h3>
            <ul style={{ listStyle: "none", padding: 0 }}>
                {history.map(item => (
                    <li
                        key={item.paymentId}
                        style={{
                            display: "flex",
                            justifyContent: "space-between",
                            padding: "8px 0",
                            borderBottom: "1px solid #eee",
                        }}
                    >
                        <div>
                            <strong>{item.type}</strong>
                            <div style={{ fontSize: "0.9em", color: "#555" }}>
                                {item.date}
                            </div>
                        </div>
                        <div>
                            <span
                                style={{
                                    color: item.direction === "INCOMING" ? "green" : "red",
                                    fontWeight: "bold",
                                }}
                            >
                                {item.direction === "INCOMING" ? "+" : "-"}{item.amount} {item.currency}
                            </span>
                            <div style={{ fontSize: "0.8em", color: "#999" }}>
                                {item.status}
                            </div>
                        </div>
                    </li>
                ))}
            </ul>
        </>
    )
}