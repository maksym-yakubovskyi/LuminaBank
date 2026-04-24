import {type ReactNode} from "react";
import {useAuth} from "@/application/auth/auth.context.tsx";
import {Navigate} from "react-router-dom";
import {StateMessage} from "@/presentation/ui/state_message/StateMessage.tsx";

export function ProtectedRoute({ children }: { children: ReactNode }) {
    const { user, initialized } = useAuth()

    if (!initialized) return <StateMessage>Завантаження...</StateMessage>
    if (!user) return <Navigate to="/login" replace />

    return <>{children}</>
}