import {z} from "zod";
import {useForm} from "react-hook-form";
import {zodResolver} from "@hookform/resolvers/zod";
import {Button} from "@/presentation/ui/button/Button.tsx";
import styles from "./LoginForm.module.css";
import {Input} from "@/presentation/ui/input/Input.tsx";

const loginShema = z.object({
    email: z.email({message: "Невірний email"}),
    password: z
        .string()
        .nonempty("Пароль обов'язковий")
        .min(6, "Пароль мінімум 6 символів")
        .max(32, "Пароль максимум 32 символи"),
})

type LoginFormInputs = z.infer<typeof loginShema>

interface Props {
    onSubmit: (data: LoginFormInputs) => void
    serverError: string | null
}

export function LoginForm({onSubmit,serverError}: Props) {
    const {register, handleSubmit, formState: {errors, isSubmitting}} = useForm<LoginFormInputs>({
        resolver: zodResolver(loginShema),
    })

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