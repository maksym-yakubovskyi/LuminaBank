import {useEffect, useState} from "react";
import type {BusinessProfile, UserProfile} from "@/features/types/userProfile.ts";
import UserService from "@/api/service/UserService.ts";
import {UserProfileInfoBlock} from "@/components/profile/UserProfileInfoBlock.tsx";
import {UserSettingsBlock} from "@/components/profile/UserSettingsBlock.tsx";
import type {BlockState} from "@/features/state/state.ts";
import {useAuth} from "@/features/auth/auth.context.tsx";
import {BusinessProfileInfoBlock} from "@/components/profile/BusinessProfileInfoBlock.tsx";
import {BusinessSettingsBlock} from "@/components/profile/BusinessSettingsBlock.tsx";

type ProfileState =
    | { type: "INDIVIDUAL_USER"; data: UserProfile}
    | { type: "BUSINESS_USER"; data: BusinessProfile}

export default function UserProfilePage() {
    const { user } = useAuth()

    const [profileState, setProfileState] = useState<BlockState<ProfileState>>({
        isLoading: true,
        data: null,
    })

    useEffect(() => {
        void loadProfile()
    },[])

    async function loadProfile(){
        try{
            if (user?.role === "BUSINESS_USER") {
                const profile = await UserService.getBusinessProfile()
                setProfileState({
                    isLoading:false,
                    data:{
                        type: "BUSINESS_USER",
                        data: profile,
                    }
                })
            }else{
                const profile = await UserService.getProfile()
                setProfileState({
                    isLoading:false,
                    data:{
                        type: "INDIVIDUAL_USER",
                        data: profile,
                    }
                })
            }
        }catch (e) {
            console.error("User profile load failed", e)
            setProfileState({ isLoading: true, data: null })
        }
    }

    const profile = profileState.data
    if (!profile) return null

    return (
        <>
            <section
                style={{
                    border: "1px solid #ddd",
                    padding: "16px",
                }}
            >
                {profile.type === "BUSINESS_USER" ? (
                    <BusinessProfileInfoBlock
                        business={profile.data}
                        loading={profileState.isLoading}
                    />
                ):(
                    <UserProfileInfoBlock
                        user={profile.data}
                        loading={profileState.isLoading}
                    />
                )}
            </section>

            <section
                style={{
                    border: "1px solid #ddd",
                    padding: "16px",
                }}
            >
                {profile.type === "BUSINESS_USER" ? (
                    <BusinessSettingsBlock
                        business={profile.data}
                        onUpdate={(b)=>
                            setProfileState({
                                isLoading:false,
                                data:{
                                    type: "BUSINESS_USER",
                                    data:b
                                }
                            })
                        }
                        loading={profileState.isLoading}
                    />
                ):(
                    <UserSettingsBlock
                        user={profile.data}
                        onUpdate={(u) =>
                            setProfileState({
                                isLoading: false,
                                data: {
                                    type: "INDIVIDUAL_USER",
                                    data: u,
                                }
                            })
                        }
                        loading={profileState.isLoading}
                    />
                )}
            </section>
        </>
    )
}