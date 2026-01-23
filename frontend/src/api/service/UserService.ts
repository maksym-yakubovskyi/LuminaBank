import type {UserProfile, UserUpdateDto} from "@/features/types/userProfile.ts"
import {api} from "@/api/api.ts"

export default class UserService {

    static async getProfile(): Promise<UserProfile> {
        const response = await api.get("/users/me")
        return response.data
    }

    static async updateProfile(data: UserUpdateDto): Promise<UserProfile>{
        const response = await api.put("/users/me", data)
        return response.data
    }

    static async deleteProfile(): Promise<void> {
        await api.delete("/users/me")
    }
}