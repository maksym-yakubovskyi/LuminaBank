import {Navigate} from "react-router-dom";
import type {ReactNode} from "react";
import {useAuth} from "@/application/auth/auth.context.tsx";
import {StateMessage} from "@/presentation/ui/state_message/StateMessage.tsx";

export function RoleProtectedRoute({ children }: { children: ReactNode }) {
    const { user, initialized } = useAuth()

    if (!initialized)
        return <StateMessage>Завантаження...</StateMessage>

    if (!user)
        return <Navigate to="/login" replace />

    // ADMIN не може ходити по інших сторінках
    if (user.role != "ADMIN") {
        return <Navigate to="/dashboard" replace />
    }

    return <>{children}</>
}