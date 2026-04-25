import {api} from "@/infrastructure/http/api-client.ts";
import type {Account} from "@/domain/account/account.types.ts";

export default class AccountAdminService {

    static async getAccountsByUserId(userId: number): Promise<Account[]> {
        const response = await api.get(`/accounts/admin/user/${userId}`)
        return response.data
    }
}