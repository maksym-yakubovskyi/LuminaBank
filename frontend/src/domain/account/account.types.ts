import type {Currency} from "@/domain/shared/currency.enum.ts";
import type {AccountType} from "@/domain/account/account-type.enum.ts";
import type {Status} from "@/domain/shared/status.enum.ts";

export interface Account {
    id: number
    userId: number
    balance: number
    iban: string
    currency: Currency
    status: Status
    type: AccountType
    createdAt: string
    updatedAt: string
}

export interface AccountCreateDto {
    currency: Currency
    type: AccountType
}