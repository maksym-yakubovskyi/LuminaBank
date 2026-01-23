import type {LoginRequest, LoginResponse, User} from "@/features/types/authTypes.ts";
import {createContext, type ReactNode, useContext, useEffect, useState} from "react";
import {tokenStorage} from "@/features/auth/tokenStorage.ts";
import AuthService from "@/api/service/AuthService.ts";

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
        async function init() {
            await refresh();
            setInitialized(true)
        }
        init().catch(console.error)
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

    const login = async (data: LoginRequest) => {
        setServerError(null)
        try {
            const result: LoginResponse = await AuthService.loginUser(data)
            applyToken(result)

            const payload = decodeToken(result.accessToken)
            if (!payload) {
                setServerError("Не валідний токен")
                clearToken()
                setUser(null)
                return false
            }

            setUser({
                id: payload.sub,
                role: payload.role
            })

            return true
        } catch (err: any) {
            console.log(err)
            if (err.response?.status === 401) setServerError("Невірний email або пароль")
            else setServerError("Помилка сервера")
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
            setServerError("Помилка сервера")
        }
    }

    const logoutAll = async (): Promise<void> => {
        setServerError(null)
        try{
            await AuthService.logoutAll()
            clearToken()
        }catch (err: any) {
            console.log(err)
            setServerError("Помилка сервера")
        }
    }

    const refresh = async (): Promise<boolean> => {
        try {
            const result: LoginResponse = await AuthService.refreshToken()
            applyToken(result)

            const payload = decodeToken(result.accessToken)
            if (!payload) {
                setServerError("Не валідний токен")
                clearToken()
                setUser(null)
                return false
            }

            setUser({
                id: payload.sub,
                role: payload.role
            })
            return true
        } catch(err: any) {
            console.log(err)
            tokenStorage.setToken(null)
            tokenStorage.setTokenType(null)
            setUser(null)
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

