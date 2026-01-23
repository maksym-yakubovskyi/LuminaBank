import {Link, useNavigate} from "react-router-dom";
import {LoginForm} from "@/components/login/LoginForm.tsx";
import {useAuth} from "@/features/auth/auth.context.tsx";

export default function LoginPage() {
    const {login, serverError} = useAuth()
    const navigate = useNavigate()

    const handleSubmit =  async (data: any)=>{
        const success = await login(data)
        if(success)navigate("/dashboard")
    }

    return (
        <div>
            <h1>Login Page</h1>
            <LoginForm onSubmit={handleSubmit} serverError={serverError}/>

            <p>
                Немає акаунта? <Link to="/register">Зареєструватися</Link>
            </p>
        </div>
    )
}