import {z} from "zod";
import {useForm} from "react-hook-form";
import {zodResolver} from "@hookform/resolvers/zod";
import {Button} from "@/components/button/Button.tsx";

const loginShema = z.object({
    email: z.email({message: "Невірний email"}),
    password: z
        .string()
        .nonempty("Пароль обов'язковий")
        .min(6, "Пароль мінімум 6 символів")
        .max(32, "Пароль максимум 32 символи"),
});

type LoginFormInputs = z.infer<typeof loginShema>;

interface Props {
    onSubmit: (data: LoginFormInputs) => void;
    serverError: string | null;
}

export function LoginForm({onSubmit,serverError}: Props) {
    const {register, handleSubmit, formState: {errors, isSubmitting}} = useForm<LoginFormInputs>({
        resolver: zodResolver(loginShema),
    });

    return (
        <form onSubmit={handleSubmit(onSubmit)}>
            {serverError && <p style={{color: "red"}}>{serverError}</p>}
            <div>
                <label>Email</label>
                <input type="email" {...register("email")}/>
                {errors.email && <p style={{ color: "red" }}>{errors.email.message}</p>}
            </div>
            <div>
                <label>Password</label>
                <input type="password" {...register("password")}/>
                {errors.password && <p style={{ color: "red" }}>{errors.password.message}</p>}
            </div>
            <Button type="submit" loading={isSubmitting}>
                Увійти
            </Button>
        </form>
    )
}