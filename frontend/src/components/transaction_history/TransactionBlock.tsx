import type {TransactionHistoryItem} from "@/features/types/transactionHistoryItem.ts";

interface Props {
    transaction: TransactionHistoryItem | null
}

export default function TransactionBlock({transaction}: Props) {
    if (!transaction) return <>Оберіть транзакцію</>

    return (
        <>
            <h3>Деталі транзакції</h3>

            <p><strong>Сума:</strong> {transaction.amount} {transaction.currency}</p>
            <p><strong>Дата:</strong> {transaction.date}</p>
            <p><strong>Напрям:</strong> {transaction.direction}</p>
            <p><strong>Статус:</strong> {transaction.status}</p>
            {transaction.description && (
                <p>
                    <strong>Опис:</strong> {transaction.description}
                </p>
            )}
        </>
    )
}