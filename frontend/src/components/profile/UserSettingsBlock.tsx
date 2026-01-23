import type {UserProfile} from "@/features/types/userProfile.ts";
import {UserEditForm} from "@/components/profile/UserEditForm.tsx";
import {LogoutButtons} from "@/components/profile/LogoutButtons.tsx";
import {DeleteAccountBlock} from "@/components/profile/DeleteAccountBlock.tsx";
interface Props {
    user: UserProfile | null
    onUpdate: (user: UserProfile) => void
}
export function UserSettingsBlock({user, onUpdate,}: Props) {
    return (
        <>
            <h3>Налаштування профілю та безпеки</h3>

            <UserEditForm user={user} onUpdate={onUpdate} />

            <hr style={{ margin: "24px 0" }} />

            <LogoutButtons />

            <DeleteAccountBlock />
        </>
    )
}