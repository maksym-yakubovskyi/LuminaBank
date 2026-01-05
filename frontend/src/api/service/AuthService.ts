import type {
    LoginRequest,
    LoginResponse,
    RegisterBusinessRequest,
    RegisterClientRequest
} from "@/features/types/authTypes.ts";
import {authApi} from "@/api/authApi.ts";

export default class AuthService {

    static async loginUser(data: LoginRequest): Promise<LoginResponse> {
        const response = await authApi.post("/auth/login", data)
        return response.data
    }

    static async refreshToken(): Promise<LoginResponse> {
        const response = await authApi.post("/auth/refresh")
        return response.data
    }

    static async sendVerificationCode(email: string): Promise<void> {
        await authApi.post("/auth/verificationCode", {email})
    }

    static async registerUser(data: RegisterClientRequest): Promise<void> {
        await authApi.post("/auth/register/user", data)
    }

    static async registerBusinessUser(data: RegisterBusinessRequest): Promise<void> {
        await authApi.post("/auth/register/user/business", data)
    }
}