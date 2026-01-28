export interface TransactionHistoryItem {
    paymentId: number
    amount: number
    currency: string
    date: string
    direction: string
    status: string
    description: string | null
}