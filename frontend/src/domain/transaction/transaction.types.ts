import type {Currency} from "@/domain/shared/currency.enum.ts";
import type {PaymentDirection} from "@/domain/payment/payment-direction-enum.ts";
import type {PaymentStatus} from "@/domain/payment/payment-status-enum.ts";

export interface TransactionHistoryItem {
    paymentId: number
    amount: number
    currency: Currency
    date: string
    direction: PaymentDirection
    status: PaymentStatus
    description: string | null
}