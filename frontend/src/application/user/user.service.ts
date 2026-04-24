import type {BusinessProfile, BusinessUserUpdateDto, UserProfile, UserUpdateDto} from "@/domain/user/user-profile.types.ts"
import {api} from "@/infrastructure/http/api-client.ts"

export default class UserService {

    static async getProfile(): Promise<UserProfile> {
        const response = await api.get("/users/my")
        return response.data
    }

    static async updateProfile(data: UserUpdateDto): Promise<UserProfile>{
        const response = await api.put("/users/me", data)
        return response.data
    }

    static async deleteProfile(): Promise<void> {
        await api.delete("/users/me")
    }

    static async getBusinessProfile(): Promise<BusinessProfile> {
        const response = await api.get("/users/business-users/my")
        return response.data
    }

    static async updateBusinessProfile(data: BusinessUserUpdateDto): Promise<BusinessProfile>{
        const response = await api.put("/users/business-users/me", data)
        return response.data
    }

    static async deleteBusinessProfile(): Promise<void> {
        await api.delete("/users/business-users/me")
    }
}