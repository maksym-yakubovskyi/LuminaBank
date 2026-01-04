import {redirect} from "react-router-dom";
import {tokenStorage} from "@/features/auth/tokenStorage.ts";

export function publicLoader() {
    if (tokenStorage.getToken()) {
        throw redirect("/dashboard");
    }
    return null;
}
