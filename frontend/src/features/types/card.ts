import type {CardNetwork, CardType} from "@/features/enum/enum.ts";

export interface Card {
    id: number
    cardNumber: string
    expirationDate: string
    cvv: string
    cardType: string
    cardNetwork: string
    accountType: string
    status: string
    limit: number
    createdAt: string
    updatedAt: string
    accountId: number
}

export interface CardCreateDto {
    cardType: CardType
    cardNetwork: CardNetwork
    limit: number;
}