import type {BusinessProfile, UserProfile} from "@/domain/user/user-profile.types.ts";
import {api} from "@/infrastructure/http/api-client.ts";

export default class AdminUserService {

    static async getUserById(id: number): Promise<UserProfile> {
        const response = await api.get(`/users/admin/${id}`)
        return response.data
    }

    static async getBusinessUserById(id: number): Promise<BusinessProfile> {
        const response = await api.get(`/users/business-users/admin/${id}`)
        return response.data
    }

    /**
     * універсальний метод (пробує обидва варіанти)
     */
    static async getAnyUser(id: number): Promise<UserProfile | BusinessProfile> {
        try {
            return await this.getUserById(id)
        } catch (e) {
            return await this.getBusinessUserById(id)
        }
    }
}