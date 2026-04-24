import {api} from "@/infrastructure/http/api-client.ts";
import type {Provider} from "@/domain/business/provider.types.ts";

export default class BusinessUserService  {

    static async getProviders (category?: Provider["category"]): Promise<Provider[]> {
        const response = await api.get(`/users/business-users/providers?category=${category}`)
        return response.data
    }
}