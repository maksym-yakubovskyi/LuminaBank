import type {UserProfile} from "@/features/types/userProfile.ts";

interface Props {
    user: UserProfile | null
    loading: boolean
}

export function UserProfileInfoBlock({ user, loading }: Props) {
    if (loading) return <p>Завантаження...</p>
    if (!user) return <>Помилка завантаження</>

    return(
        <>
            <h3>Дані користувача</h3>
            <p><b>Імʼя:</b> {user.firstName} {user.lastName}</p>
            <p><b>Email:</b> {user.email}</p>
            <p><b>Телефон:</b> {user.phoneNumber}</p>
            <p><b>Дата народження:</b> {user.birthDate}</p>

            <h4>Адреса</h4>
            {user.address ? (
                <p>
                    {user.address.street} {user.address.houseNumber}, {user.address.city}<br />
                    {user.address.zipCode}, {user.address.country}
                </p>
            ) : (
                <p style={{ fontStyle: "italic", color: "#888" }}>
                    Додайте адресу
                </p>
            )}
        </>
    )
}