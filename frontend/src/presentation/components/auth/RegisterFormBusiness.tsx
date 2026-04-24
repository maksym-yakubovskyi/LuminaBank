import {z} from "zod";
import {useForm} from "react-hook-form";
import {zodResolver} from "@hookform/resolvers/zod";
import AuthService from "@/application/auth/auth.service.ts";
import {Button} from "@/presentation/ui/button/Button.tsx";
import {useState} from "react";
import {useNavigate} from "react-router-dom";
import {BusinessCategory} from "@/domain/business/business-category.enum.ts";
import {UserType} from "@/domain/auth/user-type.enum.ts";
import styles from "./RegisterForm.module.css";
import { Input } from "@/presentation/ui/input/Input";
import { Select } from "@/presentation/ui/select/Select";

const businessShema = z.object({
    email: z.email({message: "Невірний email"}),
    password: z
        .string()
        .nonempty("Пароль обов'язковий")
        .min(6, "Пароль мінімум 6 символів")
        .max(32, "Пароль максимум 32 символи"),
    confirmPassword: z.string(),
    code: z.string().min(4, "Код підтвердження обов'язковий"),
    companyName: z.string().nonempty("Назва компанії обов'язково"),
    adrpou: z.string().nonempty("ЄДРПОУ обов'язково"),
    phone: z
        .string()
        .nonempty("Номер телефону обов'язковий")
        .regex(/^\+?\d{10,15}$/, "Невірний формат телефону"),
    category: z.enum(BusinessCategory, {
        message: "Оберіть категорію",
    }),
}).refine((data) => data.password === data.confirmPassword, {
    message: "Паролі не співпадають",
    path: ["confirmPassword"],
})

type BusinessFromInputs = z.infer<typeof businessShema>

export function RegisterFormBusiness() {
    const [codeSent, setCodeSent] = useState(false)
    const navigate = useNavigate()

    const {register, handleSubmit, formState: {errors, isSubmitting},watch,} = useForm<BusinessFromInputs>({
        resolver: zodResolver(businessShema),
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

    const onSubmit = async (data: BusinessFromInputs) => {
        try {
            await AuthService.registerBusinessUser({
                email: data.email,
                password: data.password,
                verificationCode: data.code,
                phoneNumber: data.phone,
                companyName: data.companyName,
                category: data.category,
                adrpou: data.adrpou,
                userType: UserType.BUSINESS_USER
            })
            alert("Реєстрація пройшла успішно!");
            navigate("/login")
        } catch (e) {
            console.error("Registration failed", e)
            alert("Помилка реєстрації")
        }
    }

    return (
        <form className={styles.form} onSubmit={handleSubmit(onSubmit)}>
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

            <Input
                label="Повторіть пароль"
                type="password"
                error={errors.confirmPassword?.message}
                {...register("confirmPassword")}
            />

            <Input
                label="Назва компанії"
                error={errors.companyName?.message}
                {...register("companyName")}
            />

            <Input
                label="ЄДРПОУ"
                error={errors.adrpou?.message}
                {...register("adrpou")}
            />

            <Select
                label="Категорія"
                {...register("category")}
                error={errors.category?.message}
            >
                <option value="">Оберіть категорію</option>
                {Object.values(BusinessCategory).map(cat => (
                    <option key={cat} value={cat}>
                        {cat}
                    </option>
                ))}
            </Select>

            <Input
                label="Телефон"
                placeholder="+380..."
                error={errors.phone?.message}
                {...register("phone")}
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