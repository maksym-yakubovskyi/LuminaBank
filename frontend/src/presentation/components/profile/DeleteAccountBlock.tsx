import {useAuth} from "@/application/auth/auth.context.tsx";
import UserService from "@/application/user/user.service.ts";
import {Button} from "@/presentation/ui/button/Button.tsx";
import styles from "./DeleteAccountBlock.module.css"

export function DeleteAccountBlock() {
    const { logout } = useAuth()

    const deleteAccount = async () => {
        const confirmed = window.confirm(
            "Ви впевнені, що хочете видалити акаунт?\nЦю дію неможливо скасувати."
        )
        if (!confirmed) return

        try{
            await UserService.deleteProfile()
            await logout()
        }catch (e) {
            console.error("Delete account failed", e)
            alert("Помилка видалення")
        }
    }

    return (
        <div className={styles.container}>
            <div className={styles.text}>
                Видалення акаунту призведе до повної втрати всіх ваших даних.
                Цю дію неможливо скасувати.
            </div>

            <Button
                variant="danger"
                onClick={deleteAccount}
            >
                Видалити акаунт
            </Button>
        </div>
    )
}