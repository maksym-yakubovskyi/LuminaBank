import {useAuth} from "@/features/auth/auth.context.tsx";
import UserService from "@/api/service/UserService.ts";
import {Button} from "@/components/button/Button.tsx";

export function DeleteAccountBlock() {
    const { logout } = useAuth()

    const deleteAccount = async () => {
        const confirmed = window.confirm(
            "Ви впевнені, що хочете видалити акаунт?\nЦю дію неможливо скасувати."
        )
        if (!confirmed) return
        await UserService.deleteProfile()
        await logout()
    }

    return (
        <div
            style={{
                border: "1px solid #f44336",
                padding: "16px",
                borderRadius: "6px",
                background: "#fff5f5",
            }}
        >
            <p style={{ marginBottom: "12px" }}>
                Видалення акаунту призведе до повної втрати всіх ваших даних.
            </p>

            <Button
                onClick={deleteAccount}
                style={{
                    background: "#d32f2f",
                    color: "white",
                }}
            >
                Видалити акаунт
            </Button>
        </div>
    )
}