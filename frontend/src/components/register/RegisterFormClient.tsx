import {z} from "zod"
import {useForm} from "react-hook-form"
import {zodResolver} from "@hookform/resolvers/zod"
import AuthService from "@/api/AuthService.ts"
import {Button} from "@/components/button/Button.tsx"
import {useState} from "react"
import {UserType} from "@/features/enum/enum.ts"
import {useNavigate} from "react-router-dom";

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
    const [serverError, setServerError] = useState<string | null>(null)
    const [codeSent, setCodeSent] = useState(false)
    const navigate = useNavigate()

    const {register, handleSubmit, formState: {errors, isSubmitting},watch,} = useForm<ClientFromInputs>({
        resolver: zodResolver(clientShema),
    })

    const sendCode = async (email: string) => {
        try{
            await AuthService.sendVerificationCode(email)
            setCodeSent(true)
        }catch(err: any){
            console.log(err)
            alert("Помилка відправки коду")
        }
    }

    const onSubmit = async (data: ClientFromInputs) => {
        setServerError(null)
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
        } catch (err: any) {
            console.error(err);
            alert("Помилка реєстрації: " + (err.response?.data?.message ?? "Сервер недоступний"));
        }
    }

    const emailValue = watch("email")

    return (
        <form onSubmit={handleSubmit(onSubmit)}>
            {serverError && <p style={{ color: "red" }}>{serverError}</p>}
            <div>
                <label>Email</label>
                <input type="email" {...register("email")}/>
                {errors.email && <p style={{color: "red"}}>{errors.email.message}</p>}
            </div>
            <div>
                <input placeholder="Код підтвердження" {...register("code")} />
                {errors.code && <p style={{ color: "red" }}>{errors.code.message}</p>}
                <Button type="button" onClick={() => sendCode(emailValue)} disabled={!emailValue || codeSent}>
                    {codeSent ? "Код відправлено" : "Відправити код"}
                </Button>
            </div>
            <div>
                <label>Password</label>
                <input type="password" {...register("password")}/>
                {errors.password && <p style={{color: "red"}}>{errors.password.message}</p>}
            </div>
            <div>
                <label>Confirm Password</label>
                <input type="password" {...register("confirmPassword")} />
                {errors.confirmPassword && <p style={{ color: "red" }}>{errors.confirmPassword.message}</p>}
            </div>

            <div>
                <label>Ім'я</label>
                <input {...register("firstName")} />
                {errors.firstName && <p style={{ color: "red" }}>{errors.firstName.message}</p>}
            </div>

            <div>
                <label>Прізвище</label>
                <input {...register("lastName")} />
                {errors.lastName && <p style={{ color: "red" }}>{errors.lastName.message}</p>}
            </div>

            <div>
                <label>Телефон</label>
                <input {...register("phone")} placeholder="+380XXXXXXXXX" />
                {errors.phone && <p style={{ color: "red" }}>{errors.phone.message}</p>}
            </div>

            <div>
                <label>Дата народження</label>
                <input type="date" {...register("birthDate")} />
                {errors.birthDate && <p style={{ color: "red" }}>{errors.birthDate.message}</p>}
            </div>

            <Button type="submit" loading={isSubmitting}>
                Зареєструватися
            </Button>
        </form>
    )
}