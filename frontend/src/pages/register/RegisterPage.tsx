import {useState} from "react";
import {RegisterFormBusiness} from "@/components/register/RegisterFormBusiness.tsx";
import {RegisterFormClient} from "@/components/register/RegisterFormClient.tsx";
import {Link} from "react-router-dom";
import {RegisterTypeToggle} from "@/components/register/RegisterTypeToggle.tsx";

export default function RegisterPage() {
    const [isBusiness, setIsBusiness] = useState<boolean>(false)

    return (
        <div>
            <h1> Register Page</h1>
            {isBusiness ? (
                <RegisterFormBusiness/>
            ) : (
                <RegisterFormClient/>
            )}
            <RegisterTypeToggle
                isBusiness={isBusiness}
                onChange={setIsBusiness}
            />
            <p>
                Вже є акаунт? <Link to="/login">Увійти</Link>
            </p>
        </div>
    )
}