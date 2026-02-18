import { useAuth } from "@/features/auth/auth.context.tsx"
import UserService from "@/api/service/UserService.ts"
import { Button } from "@/components/button/Button.tsx"

export function DeleteBusinessAccountBlock() {
    const { logout } = useAuth()

    const deleteAccount = async () => {
        const confirmed = window.confirm(
            "Ви впевнені, що хочете видалити бізнес-акаунт?\nЦю дію неможливо скасувати."
        )
        if (!confirmed) return

        try {
            await UserService.deleteBusinessProfile()
            await logout()
        } catch (e) {
            console.error("Delete business account failed", e)
            alert("Помилка видалення")
        }
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
                Видалення бізнес-акаунту призведе до повної втрати всіх даних.
            </p>

            <Button
                onClick={deleteAccount}
                style={{ background: "#d32f2f", color: "white" }}
            >
                Видалити бізнес-акаунт
            </Button>
        </div>
    )
}
