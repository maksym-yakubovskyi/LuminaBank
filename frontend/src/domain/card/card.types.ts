import type {Status} from "@/domain/shared/status.enum.ts";
import type {CardType} from "@/domain/card/card-type.enum.ts";
import type {CardNetwork} from "@/domain/card/card-network.enum.ts";

export interface Card {
    id: number
    cardNumber: string
    expirationDate: string
    cvv: string
    cardType: CardType
    cardNetwork: CardNetwork
    status: Status
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