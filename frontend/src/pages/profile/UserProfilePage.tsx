import {useEffect, useState} from "react";
import type {UserProfile} from "@/features/types/userProfile.ts";
import UserService from "@/api/service/UserService.ts";
import {UserProfileInfoBlock} from "@/components/profile/UserProfileInfoBlock.tsx";
import {UserSettingsBlock} from "@/components/profile/UserSettingsBlock.tsx";
import {extractErrorMessage} from "@/api/apiError.ts";

export default function UserProfilePage() {
    const [user, setUser]= useState<UserProfile | null>(null)

    useEffect(() => {
        async function loadProfile(){
            try{
                const profile = await UserService.getProfile()
                setUser(profile)
            }catch (err: any) {
                const message = extractErrorMessage(err)
                alert("Помилка отримання" + message)
            }
        }

        loadProfile().catch(console.error)
    },[])

    return (
        <>
            <section
                style={{
                    border: "1px solid #ddd",
                    padding: "16px",
                }}
            >
                <UserProfileInfoBlock user={user} />
            </section>

            <section
                style={{
                    border: "1px solid #ddd",
                    padding: "16px",
                }}
            >
                <UserSettingsBlock user={user} onUpdate={setUser} />
            </section>
        </>
    )
}