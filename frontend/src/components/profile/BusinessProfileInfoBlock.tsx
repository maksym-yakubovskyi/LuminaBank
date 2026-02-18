import type { BusinessProfile } from "@/features/types/userProfile.ts"

interface Props {
    business: BusinessProfile | null
    loading: boolean
}

export function BusinessProfileInfoBlock({ business, loading }: Props) {
    if (loading) return <p>Завантаження...</p>
    if (!business) return <>Помилка завантаження</>

    return (
        <>
            <h3>Дані бізнес-акаунту</h3>

            <p><b>Компанія:</b> {business.companyName}</p>
            <p><b>Email:</b> {business.email}</p>
            <p><b>Телефон:</b> {business.phoneNumber}</p>
            <p><b>ЄДРПОУ:</b> {business.adrpou}</p>
            <p><b>Категорія:</b> {business.category}</p>

            {business.description && (
                <p><b>Опис:</b> {business.description}</p>
            )}

            <h4>Адреса</h4>
            {business.address ? (
                <p>
                    {business.address.street} {business.address.houseNumber},{" "}
                    {business.address.city}
                    <br />
                    {business.address.zipCode}, {business.address.country}
                </p>
            ) : (
                <p style={{ fontStyle: "italic", color: "#888" }}>
                    Додайте адресу
                </p>
            )}
        </>
    )
}