import {useState} from "react";
import {RegisterFormBusiness} from "@/presentation/components/auth/RegisterFormBusiness.tsx";
import {RegisterFormClient} from "@/presentation/components/auth/RegisterFormClient.tsx";
import {Link} from "react-router-dom";
import {ToggleGroup} from "@/presentation/ui/button/ToggleGroup.tsx";
import logo from "@/assets/images/Lumina.png";
import styles from "./RegisterPage.module.css";

export default function RegisterPage() {
    const [accountType, setAccountType] = useState<"client" | "business">("client");

    return (
        <div className={styles.container}>
            <div className={styles.formWrapper}>

                <img src={logo} className={styles.logo} alt="Logo"/>

                <div className={styles.card}>

                    <h1 className={styles.title}>
                        Реєстрація
                    </h1>

                    <ToggleGroup
                        value={accountType}
                        onChange={setAccountType}
                        options={[
                            {
                                value: "client",
                                label: "Фізична особа",
                            },
                            {
                                value: "business",
                                label: "Бізнес",
                            },
                        ]}
                    />

                    {accountType === "business"
                        ? <RegisterFormBusiness/>
                        : <RegisterFormClient/>
                    }

                    <p className={styles.login}>
                        Вже є акаунт?{" "}
                        <Link to="/login">
                            Увійти
                        </Link>
                    </p>

                </div>

            </div>
        </div>

    );
}