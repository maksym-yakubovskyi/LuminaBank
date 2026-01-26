import {useState} from "react";
import {RegisterFormBusiness} from "@/components/register/RegisterFormBusiness.tsx";
import {RegisterFormClient} from "@/components/register/RegisterFormClient.tsx";
import {Link} from "react-router-dom";
import {ToggleGroup} from "@/components/button/ToggleGroup.tsx";

export default function RegisterPage() {
    const [accountType, setAccountType] = useState<"client" | "business">("client");

    return (
        <div>
            <h1>Register Page</h1>

            {accountType === "business" ? <RegisterFormBusiness /> : <RegisterFormClient />}

            <ToggleGroup
                value={accountType}
                onChange={setAccountType}
                options={[
                    { value: "client", label: "Фізична особа" },
                    { value: "business", label: "Бізнес-клієнт" },
                ]}
            />

            <p>
                Вже є акаунт? <Link to="/login">Увійти</Link>
            </p>
        </div>
    );
}