import {z} from "zod";
import {useForm} from "react-hook-form";
import {zodResolver} from "@hookform/resolvers/zod";
import {Button} from "@/presentation/ui/button/Button.tsx";
import styles from "./LoginForm.module.css";
import {Input} from "@/presentation/ui/input/Input.tsx";
import {useState} from "react";
import AuthService from "@/application/auth/auth.service.ts";

const loginShema = z.object({
    email: z.email({message: "Невірний email"}),
    password: z
        .string()
        .nonempty("Пароль обов'язковий")
        .min(6, "Пароль мінімум 6 символів")
        .max(32, "Пароль максимум 32 символи"),
    code: z.string().min(4, "Код підтвердження обов'язковий"),
})

type LoginFormInputs = z.infer<typeof loginShema>

interface Props {
    onSubmit: (data: LoginFormInputs) => void
    serverError: string | null
}

export function LoginForm({onSubmit,serverError}: Props) {
    const [codeSent, setCodeSent] = useState(false)

    const {register, handleSubmit, formState: {errors, isSubmitting},watch} = useForm<LoginFormInputs>({
        resolver: zodResolver(loginShema),
    })

    const emailValue = watch("email")

    const sendCode = async () => {
        if (!emailValue) return

        try {
            await AuthService.sendVerificationCode(emailValue)
            setCodeSent(true)
        } catch (e) {
            console.error(e)
            alert("Помилка відправки коду")
        }
    }

    return (
        <form className={styles.form} onSubmit={handleSubmit(onSubmit)}>
            {serverError && (
                <p className={styles.serverError}>{serverError}</p>
            )}

            <Input
                type="email"
                label="Email"
                error={errors.email?.message}
                {...register("email")}
            />

            {codeSent && (
                <Input
                    label="Код підтвердження"
                    placeholder="1234"
                    error={errors.code?.message}
                    {...register("code")}
                />
            )}

            {!codeSent && (
                <Button
                    type="button"
                    onClick={sendCode}
                    disabled={!emailValue}
                >
                    Отримати код
                </Button>
            )}

            <Input
                type="password"
                label="Password"
                error={errors.password?.message}
                {...register("password")}
            />

            <Button type="submit" loading={isSubmitting}>
                Увійти
            </Button>
        </form>
    )
}