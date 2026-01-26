import {api} from "@/api/api.ts";
import type {Card, CardCreateDto} from "@/features/types/card.ts";

export default class CardService {
    static async getCardsByAccount(accountId: number): Promise<Card[]> {
        const response = await api.get(`/cards/${accountId}`)
        return response.data
    }

    static async createCard(accountId: number, data: CardCreateDto) {
        const response = await api.post(`/cards/${accountId}`, data)
        return response.data
    }

    static async getMyCards(): Promise<Card[]> {
        const response = await api.get(`/cards/my`)
        return response.data
    }
}