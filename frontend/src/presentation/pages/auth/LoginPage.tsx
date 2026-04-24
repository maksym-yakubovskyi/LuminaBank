import {Link, useNavigate} from "react-router-dom";
import {LoginForm} from "@/presentation/components/auth/LoginForm.tsx";
import {useAuth} from "@/application/auth/auth.context.tsx";

import logo from "@/assets/images/Lumina.png";
import styles from "./LoginPage.module.css";

export default function LoginPage() {
    const {login, serverError} = useAuth()
    const navigate = useNavigate()

    const handleSubmit =  async (data: any)=>{
        const success = await login(data)
        if(success)navigate("/dashboard")
    }

    return (
        <div className={styles.container}>
            <div className={styles.formWrapper}>

                <img src={logo} alt="Logo" className={styles.logo}/>

                <div className={styles.card}>
                    <LoginForm onSubmit={handleSubmit} serverError={serverError}/>

                    <p className={styles.register}>
                        Немає акаунта?{" "}
                        <Link to="/register">Зареєструватися</Link>
                    </p>
                </div>

            </div>
        </div>
    )
}