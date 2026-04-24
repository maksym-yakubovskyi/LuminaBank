import {z} from "zod"
import {useForm} from "react-hook-form"
import {zodResolver} from "@hookform/resolvers/zod"
import AuthService from "@/application/auth/auth.service.ts"
import {Button} from "@/presentation/ui/button/Button.tsx"
import {useState} from "react"
import {useNavigate} from "react-router-dom";
import {UserType} from "@/domain/auth/user-type.enum.ts";
import styles from "./RegisterForm.module.css";
import { Input } from "@/presentation/ui/input/Input";

const clientShema = z.object({
    email: z.email({message: "Невірний email"}),
    password: z
        .string()
        .nonempty("Пароль обов'язковий")
        .min(6, "Пароль мінімум 6 символів")
        .max(32, "Пароль максимум 32 символи"),
    confirmPassword: z.string(),
    code: z.string().min(4, "Код підтвердження обов'язковий"),
    firstName: z.string().nonempty("Ім'я обов'язкове"),
    lastName: z.string().nonempty("Прізвище обов'язкове"),
    phone: z
        .string()
        .nonempty("Номер телефону обов'язковий")
        .regex(/^\+?\d{10,15}$/, "Невірний формат телефону"),
    birthDate: z.string().nonempty("Дата народження обов'язкова"),
}).refine((data) => data.password === data.confirmPassword, {
    message: "Паролі не співпадають",
    path: ["confirmPassword"],
})

type ClientFromInputs = z.infer<typeof clientShema>

export function RegisterFormClient() {
    const [codeSent, setCodeSent] = useState(false)
    const navigate = useNavigate()

    const {register, handleSubmit, formState: {errors, isSubmitting},watch,} = useForm<ClientFromInputs>({
        resolver: zodResolver(clientShema),
    })

    const emailValue = watch("email")

    const sendCode = async (email: string) => {
        if (!email) return

        try{
            await AuthService.sendVerificationCode(email)
            setCodeSent(true)
        } catch (e) {
            console.error("Send verification code failed", e)
            alert("Помилка відправки коду")
        }
    }

    const onSubmit = async (data: ClientFromInputs) => {
        try {
            await AuthService.registerUser({
                email: data.email,
                password: data.password,
                verificationCode: data.code,
                firstName: data.firstName,
                lastName: data.lastName,
                phoneNumber: data.phone,
                birthDate: data.birthDate,
                userType: UserType.INDIVIDUAL_USER
            })
            alert("Реєстрація пройшла успішно!");
            navigate("/login")
        } catch (e) {
            console.error("Registration failed", e)
            alert("Помилка реєстрації")
        }
    }

    return (
        <form className={styles.form} onSubmit={handleSubmit(onSubmit)} >
            <Input
                label="Email"
                type="email"
                error={errors.email?.message}
                {...register("email")}
            />
            <div className={styles.row}>
                <Input
                    label="Код"
                    placeholder="1234"
                    error={errors.code?.message}
                    {...register("code")}
                />

                <Button
                    type="button"
                    className={styles.codeButton}
                    onClick={() => sendCode(emailValue)}
                    disabled={!emailValue || codeSent}
                >
                    {codeSent ? "Надіслано" : "Код"}
                </Button>
            </div>

            <Input
                label="Пароль"
                type="password"
                error={errors.password?.message}
                {...register("password")}
            />
            <Input label="Повторіть пароль"
                   type="password"
                   error={errors.confirmPassword?.message}
                {...register("confirmPassword")}
            />

            <Input
                label="Імʼя"
                error={errors.firstName?.message}
                {...register("firstName")}
            />

            <Input
                label="Прізвище"
                error={errors.lastName?.message}
                {...register("lastName")}
            />

            <Input
                label="Телефон"
                placeholder="+380..."
                error={errors.phone?.message}
                {...register("phone")}
            />

            <Input
                label="Дата народження"
                type="date"
                error={errors.birthDate?.message}
                {...register("birthDate")}
            />

            <div className={styles.submit}>
                <Button type="submit" loading={isSubmitting}
                >
                    Зареєструватися
                </Button>
            </div>
        </form>
    )
}