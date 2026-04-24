import type {TransactionHistoryItem} from "@/domain/transaction/transaction.types.ts"
import {api} from "@/infrastructure/http/api-client.ts"

export default class TransactionHistoryService {
    static async getTransactionHistory(accountId: number): Promise<TransactionHistoryItem[]> {
        const limit: number = 3
        const response = await api.get(`/payments/history/limit`, {
            params: {
                limit,
                accountId
            }
        })
        return response.data
    }

    static async getAllTransactionHistory(accountId: number): Promise<TransactionHistoryItem[]> {
        const response = await api.get(`/payments/history/all`, {
            params: {
                accountId
            }
        })
        return response.data
    }
}