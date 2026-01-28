import {type ReactNode} from "react";
import {useAuth} from "@/features/auth/auth.context.tsx";
import {Navigate} from "react-router-dom";

export function ProtectedRoute({ children }: { children: ReactNode }) {
    const { user, initialized } = useAuth()

    if (!initialized) return <div>Завантаження...</div>
    if (!user) return <Navigate to="/login" replace />

    return <>{children}</>
}