import type {
    LoginRequest,
    LoginResponse,
    RegisterBusinessRequest,
    RegisterClientRequest
} from "@/domain/auth/auth.types.ts"
import {api} from "@/infrastructure/http/api-client.ts"
import {refreshClient} from "@/infrastructure/http/api-refresh-client.ts";

export default class AuthService {

    static async loginUser(data: LoginRequest): Promise<LoginResponse> {
        const response = await api.post("/auth/login", data)
        return response.data
    }

    static async refreshToken(): Promise<LoginResponse> {
        const response = await refreshClient.post("/auth/refresh")
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