export interface TransactionHistoryItem {
    paymentId: number
    type: string
    amount: number
    currency: string
    date: string
    direction: string
    status: string
    description: string | null
}