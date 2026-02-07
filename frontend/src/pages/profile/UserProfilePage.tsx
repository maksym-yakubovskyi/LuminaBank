import {useEffect, useState} from "react";
import type {UserProfile} from "@/features/types/userProfile.ts";
import UserService from "@/api/service/UserService.ts";
import {UserProfileInfoBlock} from "@/components/profile/UserProfileInfoBlock.tsx";
import {UserSettingsBlock} from "@/components/profile/UserSettingsBlock.tsx";
import type {BlockState} from "@/features/state/state.ts";

export default function UserProfilePage() {
    const [userState, setUserState] = useState<BlockState<UserProfile>>({
        isLoading: true,
        data: null,
    })

    useEffect(() => {
        void loadProfile()
    },[])

    async function loadProfile(){
        try{
            const profile = await UserService.getProfile()
            setUserState({ isLoading: false, data: profile })

        }catch (e) {
            console.error("User profile load failed", e)
            setUserState({ isLoading: true, data: null })
        }
    }

    return (
        <>
            <section
                style={{
                    border: "1px solid #ddd",
                    padding: "16px",
                }}
            >
                <UserProfileInfoBlock
                    user={userState.data}
                    loading={userState.isLoading}
                />
            </section>

            <section
                style={{
                    border: "1px solid #ddd",
                    padding: "16px",
                }}
            >
                <UserSettingsBlock
                    user={userState.data}
                    onUpdate={(u) =>
                        setUserState({ isLoading: false, data: u })
                    }
                    loading={userState.isLoading}
                />
            </section>
        </>
    )
}