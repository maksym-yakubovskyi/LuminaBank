import {api} from "@/api/api.ts";
import type {Provider} from "@/features/types/provider.ts";

export default class BusinessUserService  {

    static async getProviders (category?: Provider["category"]): Promise<Provider[]> {
        const response = await api.get(`/users/business-users/providers?category=${category}`)
        return response.data
    }
}