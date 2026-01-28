import axios from "axios";
import AuthService from "@/api/service/AuthService.ts";
import type {LoginResponse} from "@/features/types/authTypes.ts";
import {tokenStorage} from "@/features/auth/tokenStorage.ts";

export const api = axios.create({
    baseURL: "http://localhost:8080/api",
    withCredentials: true
})

api.interceptors.request.use(
    config => {
        const token = tokenStorage.getToken()
        const tokenType = tokenStorage.getTokenType()
        if (token && tokenType) {
            config.headers.Authorization = `${tokenType} ${token}`
        }
        return config
    }
)

api.interceptors.response.use(
    response => response,
    async error => {
        const originalRequest = error.config

        if (
            error.response?.status === 401 &&
            !originalRequest._retry &&
            !originalRequest.url?.includes("/auth/refresh")
        ) {
            originalRequest._retry = true

            try {
                const result: LoginResponse = await AuthService.refreshToken()
                tokenStorage.setToken(result.accessToken)
                tokenStorage.setTokenType(result.tokenType)

                originalRequest.headers.Authorization = `${result.tokenType} ${result.accessToken}`
                return api(originalRequest)
            } catch {
                tokenStorage.setToken(null)
                tokenStorage.setTokenType(null)
            }
        }
        return Promise.reject(error)
    }
)