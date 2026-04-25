import type {UserProfile} from "@/domain/user/user-profile.types.ts"
import {StateMessage} from "@/presentation/ui/state_message/StateMessage.tsx"
import styles from "./UserInfoBlock.module.css"

interface Props {
    user: UserProfile | null
    loading: boolean
}

export function IndividualUserInfoBlock({ user, loading }: Props) {

    if (loading)
        return <StateMessage>Завантаження...</StateMessage>

    if (!user)
        return <StateMessage>Користувача не знайдено</StateMessage>

    return (
        <section className={styles.container}>

            <h3 className={styles.title}>
                Фізична особа
            </h3>

            <div className={styles.section}>

                <div className={styles.grid}>
                    <div className={styles.label}>ID</div>
                    <div className={styles.value}>{user.id}</div>

                    <div className={styles.label}>Імʼя</div>
                    <div className={styles.value}>
                        {user.firstName} {user.lastName}
                    </div>

                    <div className={styles.label}>Email</div>
                    <div className={styles.value}>{user.email}</div>

                    <div className={styles.label}>Телефон</div>
                    <div className={styles.value}>{user.phoneNumber}</div>

                    <div className={styles.label}>Дата народження</div>
                    <div className={styles.value}>
                        {new Date(user.birthDate).toLocaleDateString("uk-UA")}
                    </div>
                </div>

            </div>

            {user.address && (
                <div className={styles.section}>
                    <div className={styles.sectionTitle}>
                        Адреса
                    </div>

                    <div className={styles.value}>
                        {user.address.city}, {user.address.street} {user.address.houseNumber}
                    </div>
                </div>
            )}

        </section>
    )
}