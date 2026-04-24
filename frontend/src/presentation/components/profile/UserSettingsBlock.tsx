import type {UserProfile} from "@/domain/user/user-profile.types.ts";
import {UserEditForm} from "@/presentation/components/profile/UserEditForm.tsx";
import {LogoutButtons} from "@/presentation/components/profile/LogoutButtons.tsx";
import {DeleteAccountBlock} from "@/presentation/components/profile/DeleteAccountBlock.tsx";
import styles from "./UserSettingsBlock.module.css"
import {StateMessage} from "@/presentation/ui/state_message/StateMessage.tsx";

interface Props {
    user: UserProfile | null
    onUpdate: (user: UserProfile) => void
    loading: boolean
}

export function UserSettingsBlock({user, onUpdate,loading}: Props) {
    if (loading)
        return <StateMessage>Завантаження...</StateMessage>

    if (!user)
        return <StateMessage>Помилка завантаження</StateMessage>

    return (
        <section className={styles.container}>

            <h3 className={styles.title}>
                Налаштування профілю та безпеки
            </h3>

            <div className={styles.section}>
                <h4 className={styles.sectionTitle}>
                    Редагування профілю
                </h4>

                <UserEditForm
                    user={user}
                    onUpdate={onUpdate}
                />
            </div>

            <div className={styles.divider} />

            <div className={styles.section}>
                <h4 className={styles.sectionTitle}>
                    Безпека
                </h4>

                <LogoutButtons />
            </div>

            <div className={styles.divider} />

            <div className={styles.section}>
                <div className={styles.dangerTitle}>
                    Небезпечна зона
                </div>

                <DeleteAccountBlock />
            </div>

        </section>
    )
}