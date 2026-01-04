import {redirect} from "react-router-dom";
import {tokenStorage} from "@/features/auth/tokenStorage.ts";

export function protectedLoader() {
    if (!tokenStorage.getToken()) {
        throw redirect("/login")
    }
    return null
}
