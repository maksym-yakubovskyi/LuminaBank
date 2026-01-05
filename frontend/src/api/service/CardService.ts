import {api} from "@/api/api.ts";
import type {Card} from "@/features/types/card.ts";

export default class CardService {
    static async getCardsByAccount(accountId: number): Promise<Card[]> {
        const response = await api.get(`/cards/${accountId}`);
        return response.data;
    }
}