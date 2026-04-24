import type { BusinessProfile } from "@/domain/user/user-profile.types.ts"
import { BusinessEditForm } from "./BusinessEditForm.tsx"
import { LogoutButtons } from "@/presentation/components/profile/LogoutButtons.tsx"
import { DeleteBusinessAccountBlock } from "./DeleteBusinessAccountBlock.tsx"
import {StateMessage} from "@/presentation/ui/state_message/StateMessage.tsx";
import styles from "./UserSettingsBlock.module.css"

interface Props {
    business: BusinessProfile | null
    onUpdate: (user: BusinessProfile) => void
    loading: boolean
}

export function BusinessSettingsBlock({ business, onUpdate, loading }: Props) {
    if (loading) return <StateMessage>Завантаження...</StateMessage>
    if (!business) return <StateMessage>Помилка завантаження</StateMessage>

    return (
        <section className={styles.container}>

            <h3 className={styles.title}>
                Налаштування бізнес-профілю та безпеки
            </h3>

            <div className={styles.section}>
                <h4 className={styles.sectionTitle}>
                    Редагування профілю
                </h4>

                <BusinessEditForm
                    business={business}
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

                <DeleteBusinessAccountBlock />
            </div>

        </section>
    )
}
