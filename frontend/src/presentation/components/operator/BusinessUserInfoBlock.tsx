import type {BusinessProfile} from "@/domain/user/user-profile.types.ts"
import {StateMessage} from "@/presentation/ui/state_message/StateMessage.tsx"
import styles from "./UserInfoBlock.module.css"

interface Props {
    business: BusinessProfile | null
    loading: boolean
}

export function BusinessUserInfoBlock({ business, loading }: Props) {

    if (loading)
        return <StateMessage>Завантаження...</StateMessage>

    if (!business)
        return <StateMessage>Користувача не знайдено</StateMessage>

    return (
        <section className={styles.container}>

            <h3 className={styles.title}>
                Бізнес-клієнт
            </h3>

            <div className={styles.section}>

                <div className={styles.grid}>
                    <div className={styles.label}>ID</div>
                    <div className={styles.value}>{business.id}</div>

                    <div className={styles.label}>Компанія</div>
                    <div className={styles.value}>{business.companyName}</div>

                    <div className={styles.label}>Email</div>
                    <div className={styles.value}>{business.email}</div>

                    <div className={styles.label}>Телефон</div>
                    <div className={styles.value}>{business.phoneNumber}</div>

                    <div className={styles.label}>ЄДРПОУ</div>
                    <div className={styles.value}>{business.adrpou}</div>

                    <div className={styles.label}>Категорія</div>
                    <div className={styles.value}>{business.category}</div>
                </div>

            </div>

            {business.address && (
                <div className={styles.section}>
                    <div className={styles.sectionTitle}>
                        Адреса
                    </div>

                    <div className={styles.value}>
                        {business.address.city}, {business.address.street} {business.address.houseNumber}
                    </div>
                </div>
            )}

        </section>
    )
}