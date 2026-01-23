import type {AccountType, Currency} from "@/features/enum/enum.ts";

export interface Account {
    id: number
    balance: number
    iban: string
    currency: string
    status: string
    type: string
    createdAt: string
    updatedAt: string
}

export interface AccountCreateDto {
    currency: Currency
    type: AccountType
}