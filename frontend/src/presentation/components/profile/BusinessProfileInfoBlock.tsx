import type { BusinessProfile } from "@/domain/user/user-profile.types.ts"
import styles from "./BusinessProfileInfoBlock.module.css"
import {StateMessage} from "@/presentation/ui/state_message/StateMessage.tsx";

interface Props {
    business: BusinessProfile | null
    loading: boolean
}

export function BusinessProfileInfoBlock({ business, loading }: Props) {
    if (loading)
        return <StateMessage>Завантаження...</StateMessage>

    if (!business)
        return <StateMessage>Помилка завантаження</StateMessage>

    return (
        <section className={styles.container}>

            <h3 className={styles.title}>
                Дані бізнес-акаунту
            </h3>

            <div className={styles.section}>
                <div className={styles.grid}>

                    <div className={styles.label}>Компанія</div>
                    <div className={styles.value}>
                        {business.companyName}
                    </div>

                    <div className={styles.label}>Email</div>
                    <div className={styles.value}>
                        {business.email}
                    </div>

                    <div className={styles.label}>Телефон</div>
                    <div className={styles.value}>
                        {business.phoneNumber}
                    </div>

                    <div className={styles.label}>ЄДРПОУ</div>
                    <div className={styles.value}>
                        {business.adrpou}
                    </div>

                    <div className={styles.label}>Категорія</div>
                    <div className={styles.value}>
                        {business.category}
                    </div>

                </div>
            </div>

            {business.description && (
                <div className={styles.section}>
                    <h4 className={styles.sectionTitle}>
                        Опис
                    </h4>

                    <div className={styles.descriptionBox}>
                        {business.description}
                    </div>
                </div>
            )}

            <div className={styles.section}>
                <h4 className={styles.sectionTitle}>
                    Адреса
                </h4>

                <div className={styles.addressBox}>
                    {business.address ? (
                        <div>
                            {business.address.street} {business.address.houseNumber}
                            <br />
                            {business.address.city}, {business.address.zipCode}
                            <br />
                            {business.address.country}
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