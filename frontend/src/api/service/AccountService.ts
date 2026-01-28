import type {Account, AccountCreateDto} from "@/features/types/account.ts"
import {api} from "@/api/api.ts"

export default class AccountService {
    static async getMyAccounts(): Promise<Account[]> {
        const response = await api.get(`/accounts/my`)
        return response.data
    }

    static async createAccount(data: AccountCreateDto) {
        const res = await api.post("/accounts", data);
        return res.data
    }
}