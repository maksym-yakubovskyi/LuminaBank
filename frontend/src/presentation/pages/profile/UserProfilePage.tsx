import {useEffect, useState} from "react";
import type {BusinessProfile, UserProfile} from "@/domain/user/user-profile.types.ts";
import UserService from "@/application/user/user.service.ts";
import {UserProfileInfoBlock} from "@/presentation/components/profile/UserProfileInfoBlock.tsx";
import {UserSettingsBlock} from "@/presentation/components/profile/UserSettingsBlock.tsx";
import type {BlockState} from "@/domain/shared/block-state.type.ts";
import {useAuth} from "@/application/auth/auth.context.tsx";
import {BusinessProfileInfoBlock} from "@/presentation/components/profile/BusinessProfileInfoBlock.tsx";
import {BusinessSettingsBlock} from "@/presentation/components/profile/BusinessSettingsBlock.tsx";
import {Grid} from "@/presentation/ui/grid/Grid.tsx";

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
        <Grid
            left={
                <>
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
                </>
            }
            right={
                <>
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
                </>
            }
        />
    )
}