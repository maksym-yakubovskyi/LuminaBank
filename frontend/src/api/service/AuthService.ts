import type {
    LoginRequest,
    LoginResponse,
    RegisterBusinessRequest,
    RegisterClientRequest
} from "@/features/types/authTypes.ts"
import {api} from "@/api/api.ts"

export default class AuthService {

    static async loginUser(data: LoginRequest): Promise<LoginResponse> {
        const response = await api.post("/auth/login", data)
        return response.data
    }

    static async refreshToken(): Promise<LoginResponse> {
        const response = await api.post("/auth/refresh")
        return response.data
    }

    static async sendVerificationCode(email: string): Promise<void> {
        await api.post("/auth/verificationCode", {email})
    }

    static async registerUser(data: RegisterClientRequest): Promise<void> {
        await api.post("/auth/register/user", data)
    }

    static async registerBusinessUser(data: RegisterBusinessRequest): Promise<void> {
        await api.post("/auth/register/user/business", data)
    }

    static async logout(): Promise<void> {
        await api.post("/auth/logout")
    }

    static async logoutAll(): Promise<void> {
        await api.post("/auth/logout/all")
    }
}