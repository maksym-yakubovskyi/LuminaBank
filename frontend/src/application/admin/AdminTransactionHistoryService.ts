import type {PaymentStatus} from "@/domain/payment/payment-status-enum.ts";
import type {TransactionHistoryItem} from "@/domain/transaction/transaction.types.ts";
import {api} from "@/infrastructure/http/api-client.ts";

export default class AdminTransactionHistoryService {

    static async getTransactions(
        userId: number,
        accountId: number,
        status?: PaymentStatus
    ): Promise<TransactionHistoryItem[]> {

        const response = await api.get("/payments/admin/user/history", {
            params: {
                userId,
                accountId,
                status // optional
            }
        })

        return response.data
    }
}