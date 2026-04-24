import type {UserProfile} from "@/domain/user/user-profile.types.ts";
import styles from "./UserProfileInfoBlock.module.css"
import {StateMessage} from "@/presentation/ui/state_message/StateMessage.tsx";

interface Props {
    user: UserProfile | null
    loading: boolean
}

export function UserProfileInfoBlock({ user, loading }: Props) {
    if (loading)
        return <StateMessage>Завантаження...</StateMessage>

    if (!user)
        return <StateMessage>Помилка завантаження</StateMessage>

    return(
        <section className={styles.container}>

            <h3 className={styles.title}>
                Дані користувача
            </h3>

            <div className={styles.section}>
                <div className={styles.grid}>
                    <div className={styles.label}>Імʼя</div>
                    <div className={styles.value}>
                        {user.firstName} {user.lastName}
                    </div>

                    <div className={styles.label}>Email</div>
                    <div className={styles.value}>
                        {user.email}
                    </div>

                    <div className={styles.label}>Телефон</div>
                    <div className={styles.value}>
                        {user.phoneNumber}
                    </div>

                    <div className={styles.label}>Дата народження</div>
                    <div className={styles.value}>
                        {new Date(user.birthDate).toLocaleDateString("uk-UA")}
                    </div>
                </div>
            </div>

            <div className={styles.section}>
                <h4 className={styles.sectionTitle}>
                    Адреса
                </h4>

                <div className={styles.addressBox}>
                    {user.address ? (
                        <div>
                            {user.address.street} {user.address.houseNumber}
                            <br />
                            {user.address.city}, {user.address.zipCode}
                            <br />
                            {user.address.country}
                        </div>
                    ) : (
                        <div className={styles.emptyAddress}>
                            Додайте адресу
                        </div>
                    )}
                </div>
            </div>

        </section>
    )
}