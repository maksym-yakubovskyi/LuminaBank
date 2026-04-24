import {type ReactNode} from "react";
import {useAuth} from "@/application/auth/auth.context.tsx";
import {Navigate} from "react-router-dom";
import {StateMessage} from "@/presentation/ui/state_message/StateMessage.tsx";

export function PublicRoute({ children }: { children: ReactNode }) {
    const { user, initialized } = useAuth()

    if (!initialized) return <StateMessage>Завантаження...</StateMessage>
    if (user) return <Navigate to="/dashboard" replace />

    return <>{children}</>
}