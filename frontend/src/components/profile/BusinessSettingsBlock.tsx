import type { BusinessProfile } from "@/features/types/userProfile.ts"
import { BusinessEditForm } from "./BusinessEditForm.tsx"
import { LogoutButtons } from "@/components/profile/LogoutButtons.tsx"
import { DeleteBusinessAccountBlock } from "./DeleteBusinessAccountBlock.tsx"

interface Props {
    business: BusinessProfile | null
    onUpdate: (user: BusinessProfile) => void
    loading: boolean
}

export function BusinessSettingsBlock({ business, onUpdate, loading }: Props) {
    if (loading) return <p>Завантаження...</p>
    if (!business) return <>Помилка завантаження</>

    return (
        <>
            <h3>Налаштування бізнес-профілю та безпеки</h3>

            <BusinessEditForm business={business} onUpdate={onUpdate} />

            <hr style={{ margin: "24px 0" }} />

            <LogoutButtons />

            <DeleteBusinessAccountBlock />
        </>
    )
}
