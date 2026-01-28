import {Button} from "@/components/button/Button.tsx";
import {useAuth} from "@/features/auth/auth.context.tsx";

export function LogoutButtons() {
    const{logout,logoutAll,serverError}= useAuth()

    return (
        <div style={{ display: "grid", gap: "8px" }}>
            {serverError && <p style={{color: "red"}}>{serverError}</p>}

            <Button onClick={logout}>
                Вийти з поточної сесії
            </Button>

            <Button onClick={logoutAll}>
                Вийти з усіх сесій
            </Button>
        </div>
    )
}