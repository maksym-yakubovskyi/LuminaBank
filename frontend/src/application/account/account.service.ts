import type {Account, AccountCreateDto} from "@/domain/account/account.types.ts"
import {api} from "@/infrastructure/http/api-client.ts"

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