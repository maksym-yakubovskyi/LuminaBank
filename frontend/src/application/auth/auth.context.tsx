import type {LoginRequest, LoginResponse, User} from "@/domain/auth/auth.types.ts";
import {createContext, type ReactNode, useContext, useEffect, useState} from "react";
import {tokenStorage} from "@/infrastructure/security/token-storage.ts";
import AuthService from "@/application/auth/auth.service.ts";
import {extractErrorMessage} from "@/infrastructure/http/error-handler.ts";

interface AuthContextType{
    user: User | null
    isAuthenticated: boolean
    serverError: string | null
    login: (data: LoginRequest) => Promise<boolean>
    logout: () => Promise<void>
    logoutAll: () => Promise<void>
    refresh: () => Promise<boolean>
    setServerError: (msg: string | null) => void
    initialized: boolean
}

const AuthContext = createContext<AuthContextType>(null!)

export function AuthProvider({ children }: {children: ReactNode}) {
    const [user, setUser] = useState<User | null>(null)
    const [serverError, setServerError] = useState<string | null>(null)
    const [initialized, setInitialized] = useState(false)

    useEffect(() => {
        const init = async () => {
            await refresh()   // 1 раз при старті
            setInitialized(true)
        }
        void init()
    }, [])

    const decodeToken = (token: string) => {
        try {
            return JSON.parse(atob(token.split(".")[1]))
        } catch {
            return null
        }
    }

    const applyToken = (result: LoginResponse) => {
        tokenStorage.setToken(result.accessToken);
        tokenStorage.setTokenType(result.tokenType);
    }
    const clearToken = () => {
        tokenStorage.setToken(null)
        tokenStorage.setTokenType(null)
        setUser(null)
    }
    const handleAuthError = (message: string) => {
        setServerError(message)
        clearToken()
        setUser(null)
        return false
    }

    const login = async (data: LoginRequest) => {
        setServerError(null)
        try {
            const result: LoginResponse = await AuthService.loginUser(data)
            applyToken(result)

            const payload = decodeToken(result.accessToken)
            if (!payload) {
                return handleAuthError("Не валідний токен")
            }

            setUser({
                id: payload.sub,
                role: payload.roles[0]
            })

            return true
        } catch (err: any) {
            console.log(err)
            const message = extractErrorMessage(err)
            setServerError(message)
            clearToken()
            return false
        }
    }

    const logout = async (): Promise<void> => {
        setServerError(null)
        try{
            await AuthService.logout()
            clearToken()
        }catch (err: any) {
            console.log(err)
            const message = extractErrorMessage(err)
            setServerError(message)
        }
    }

    const logoutAll = async (): Promise<void> => {
        setServerError(null)
        try{
            await AuthService.logoutAll()
            clearToken()
        }catch (err: any) {
            console.log(err)
            const message = extractErrorMessage(err)
            setServerError(message)
        }
    }

    const refresh = async (): Promise<boolean> => {
        try {
            const result = await AuthService.refreshToken()
            applyToken(result)

            const payload = decodeToken(result.accessToken)
            if (!payload) return false

            setUser({
                id: payload.sub,
                role: payload.roles[0]
            })
            return true
        } catch(err: any) {
            console.log(err)
            clearToken()
            return false
        }
    }

    return (
        <AuthContext.Provider
            value={{
                user,
                isAuthenticated: !!user,
                serverError,
                login,
                logout,
                logoutAll,
                refresh,
                setServerError,
                initialized,
            }}
        >
            {children}
        </AuthContext.Provider>
    )
}

export const useAuth = () => useContext(AuthContext)