import {Button} from "@/presentation/ui/button/Button.tsx";
import {useAuth} from "@/application/auth/auth.context.tsx";
import styles from "./LogoutButtons.module.css"

export function LogoutButtons() {
    const { logout, logoutAll, serverError } = useAuth()

    return (
        <div className={styles.container}>

            {serverError &&
                (<div className={styles.error}>{serverError}</div>)}

            <Button onClick={logout}>
                Вийти з поточної сесії
            </Button>

            <Button onClick={logoutAll}>
                Вийти з усіх сесій
            </Button>
        </div>
    )
}