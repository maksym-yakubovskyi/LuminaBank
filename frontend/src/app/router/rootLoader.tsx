import {tokenStorage} from "@/features/auth/tokenStorage.ts";
import {redirect} from "react-router-dom";

export async function rootLoader() {
    const token = tokenStorage.getToken();

    if (token) return redirect("/dashboard");
    return redirect("/login");
}