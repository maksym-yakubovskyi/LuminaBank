import type {TransactionHistoryItem} from "@/features/types/transactionHistoryItem.ts";
import {api} from "@/api/api.ts";

export default class TransactionHistoryService {
    static async getTransactionHistory(accountId: number): Promise<TransactionHistoryItem[]> {
        const limit: number = 3;
        const response = await api.get(`/payments/history/${accountId}/limit?limit=${limit}`);
        return response.data;
    }
}