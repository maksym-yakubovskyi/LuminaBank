import type {UserProfile, UserUpdateDto} from "@/domain/user/user-profile.types.ts";
import {useForm} from "react-hook-form";
import UserService from "@/application/user/user.service.ts";
import {Button} from "@/presentation/ui/button/Button.tsx";
import {z} from "zod";
import {zodResolver} from "@hookform/resolvers/zod"
import {Input} from "@/presentation/ui/input/Input.tsx";
import styles from "./UserEditForm.module.css";

const userEditSchema = z.object({
    firstName: z.string().nonempty("Ім'я обов'язкове")
        .min(2, "Ім'я мінімум 2 символи")
        .max(50, "Ім'я максимум 50 символів"),

    lastName: z.string().nonempty("Прізвище обов'язкове")
        .min(2, "Прізвище мінімум 2 символи")
        .max(50, "Прізвище максимум 50 символів"),

    email: z.email({message: "Невірний email"}),

    phoneNumber: z.string().nonempty("Телефон обов'язковий")
        .regex(/^\+?[0-9\s()-]+$/, "Невірний формат телефону"),

    birthDate: z.string().nonempty("Дата народження обов'язкова"),

    street: z.string().min(2, "Вулиця мінімум 2 символи")
        .max(100, "Вулиця максимум 100 символів"),

    city: z.string().min(2, "Місто мінімум 2 символи")
        .max(100, "Місто максимум 100 символів"),

    houseNumber: z.string().max(20, "Номер будинку максимум 20 символів"),

    zipCode: z.string().min(4, "Поштовий індекс мінімум 4 символи")
        .max(12, "Поштовий індекс максимум 12 символів"),

    country: z.string().min(2, "Країна мінімум 2 символи")
        .max(60, "Країна максимум 60 символів"),
})

type FormInputs = z.infer<typeof userEditSchema>

interface Props {
    user: UserProfile | null
    onUpdate: (user: UserProfile) => void
}

export function UserEditForm({user, onUpdate}: Props) {
    const {
        register,
        handleSubmit,
        formState: {errors, isSubmitting},
    } = useForm<FormInputs>({
        resolver: zodResolver(userEditSchema),
        values: user
            ? {
                firstName: user.firstName,
                lastName: user.lastName,
                email: user.email,
                phoneNumber: user.phoneNumber,
                birthDate: user.birthDate,

                street: user.address?.street ?? "",
                city: user.address?.city ?? "",
                houseNumber: user.address?.houseNumber ?? "",
                zipCode: user.address?.zipCode ?? "",
                country: user.address?.country ?? "",
            }
            : undefined,
    })

    const onSubmit = async (data: FormInputs) => {
        try {
            const updatedUser = await UserService.updateProfile({
                firstName: data.firstName,
                lastName: data.lastName,
                email: data.email,
                phoneNumber: data.phoneNumber,
                birthDate: data.birthDate,
                street: data.street,
                city: data.city,
                houseNumber: data.houseNumber,
                zipCode: data.zipCode,
                country: data.country,
            }as UserUpdateDto)

            onUpdate(updatedUser)
        }catch (e) {
            console.error("Update profile failed", e)
            alert("Помилка оновлення")
        }
    }

    return (
        <form
            onSubmit={handleSubmit(onSubmit)}
            className={styles.form}
        >
            <div className={styles.grid}>

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
                    label="Email"
                    error={errors.email?.message}
                    {...register("email")}
                />

                <Input
                    label="Телефон"
                    error={errors.phoneNumber?.message}
                    {...register("phoneNumber")}
                />

                <Input
                    type="date"
                    label="Дата народження"
                    error={errors.birthDate?.message}
                    {...register("birthDate")}
                    className={styles.fullWidth}
                />
            </div>

            <div className={styles.sectionTitle}>
                Адреса
            </div>

            <div className={styles.grid}>
                <Input label="Вулиця" {...register("street")} error={errors.street?.message} />
                <Input label="Будинок" {...register("houseNumber")} error={errors.houseNumber?.message} />
                <Input label="Місто" {...register("city")} error={errors.city?.message} />
                <Input label="Поштовий індекс" {...register("zipCode")} error={errors.zipCode?.message} />
                <Input label="Країна" {...register("country")} error={errors.country?.message} className={styles.fullWidth} />
            </div>

            <div className={styles.actions}>
                <Button type="submit" loading={isSubmitting}>
                    Зберегти зміни
                </Button>
            </div>
        </form>
    )
}