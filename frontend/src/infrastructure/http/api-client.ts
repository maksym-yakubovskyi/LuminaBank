import axios from "axios";
import AuthService from "@/application/auth/auth.service.ts";
import {tokenStorage} from "@/infrastructure/security/token-storage.ts";

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

let refreshPromise: Promise<any> | null = null

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

            if (!refreshPromise) {
                refreshPromise = AuthService.refreshToken()
                    .then(result => {
                        tokenStorage.setToken(result.accessToken)
                        tokenStorage.setTokenType(result.tokenType)
                        refreshPromise = null
                        return result
                    })
                    .catch(err => {
                        refreshPromise = null
                        throw err
                    })
            }

            try {
                const result = await refreshPromise
                originalRequest.headers.Authorization =
                    `${result.tokenType} ${result.accessToken}`

                return api(originalRequest)
            } catch (err) {
                return Promise.reject(err)
            }
        }
        return Promise.reject(error)
    }
)