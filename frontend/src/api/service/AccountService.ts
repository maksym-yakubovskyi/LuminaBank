import type {Account} from "@/features/types/account.ts";
import {api} from "@/api/api.ts";

export default class AccountService {
        static async getMyAccounts(): Promise<Account[]> {
            const response = await api.get(`/accounts/my`)
            return response.data;
        }
}