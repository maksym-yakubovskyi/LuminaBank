import { useAuth } from "@/application/auth/auth.context.tsx"
import UserService from "@/application/user/user.service.ts"
import { Button } from "@/presentation/ui/button/Button.tsx"
import styles from "@/presentation/components/profile/DeleteAccountBlock.module.css";

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
        <div className={styles.container}>
            <div className={styles.text}>
                Видалення бізнес-акаунту призведе до повної втрати всіх даних.
            </div>

            <Button
                onClick={deleteAccount}
                variant="danger"
            >
                Видалити бізнес-акаунт
            </Button>
        </div>
    )
}
