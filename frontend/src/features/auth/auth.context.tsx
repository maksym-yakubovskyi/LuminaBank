import type {LoginRequest, LoginResponse, User} from "@/features/auth/types.ts";
import {createContext, type ReactNode, useContext, useState} from "react";
import {tokenStorage} from "@/features/auth/tokenStorage.ts";
import AuthService from "@/api/AuthService.ts";

interface AuthContextType{
    user: User | null
    isAuthenticated: boolean
    serverError: string | null
    login: (data: LoginRequest) => Promise<boolean>
    logout: () => Promise<void>
    refresh: () => Promise<boolean>
    setServerError: (msg: string | null) => void
}

const AuthContext = createContext<AuthContextType>(null!);

export function AuthProvider({ children }: {children: ReactNode}) {
    const [user, setUser] = useState<User | null>(null)
    const [serverError, setServerError] = useState<string | null>(null)

    const login = async (data: LoginRequest) => {
        setServerError(null);
        try {
            const result: LoginResponse = await AuthService.loginUser(data);

            tokenStorage.setToken(result.accessToken);
            tokenStorage.setTokenType(result.tokenType);

            const payload = JSON.parse(atob(result.accessToken.split(".")[1]))
            setUser({
                id: payload.sub,
                role: payload.role
            })

            console.log(result);
            return true;
        } catch (err: any) {
            console.log(err);
            if (err.response?.status === 401) setServerError("Невірний email або пароль");
            else setServerError("Помилка сервера");
            return false;
        }
    }

    const logout = async (): Promise<void> => {
        tokenStorage.setToken(null)
        tokenStorage.setTokenType(null)
        setUser(null)
    }

    const refresh = async (): Promise<boolean> => {
        try {
            const result: LoginResponse = await AuthService.refreshToken();
            tokenStorage.setToken(result.accessToken);
            tokenStorage.setTokenType(result.tokenType);
            const payload = JSON.parse(atob(result.accessToken.split(".")[1]))
            setUser({
                id: payload.id,
                role: payload.role
            })
            return true;
        } catch {
            tokenStorage.setToken(null);
            tokenStorage.setTokenType(null);
            setUser(null);
            return false;
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
                refresh,
                setServerError
            }}
        >
            {children}
        </AuthContext.Provider>
    )
}

export const useAuth = () => AuthContext && useContext(AuthContext);

