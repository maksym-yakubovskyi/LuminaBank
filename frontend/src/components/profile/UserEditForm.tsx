import type {UserProfile, UserUpdateDto} from "@/features/types/userProfile.ts";
import {useForm} from "react-hook-form";
import UserService from "@/api/service/UserService.ts";
import {Button} from "@/components/button/Button.tsx";
import {z} from "zod";
import {zodResolver} from "@hookform/resolvers/zod"

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

    if (!user) return <p>Завантаження...</p>

    const onSubmit = async (data: FormInputs) => {
        const dto: UserUpdateDto = {
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
        }

        const updatedUser = await UserService.updateProfile(dto);
        onUpdate(updatedUser);
    }

    return (
        <form
            onSubmit={handleSubmit(onSubmit)}
            style={{display: "grid", gap: "12px"}}
        >
            <h4>Редагування профілю</h4>

            <div>
                <input {...register("firstName")} placeholder="Імʼя" />
                {errors.firstName && <p style={{ color: "red" }}>{errors.firstName.message}</p>}
            </div>
            <div>
                <input {...register("lastName")} placeholder="Прізвище" />
                {errors.lastName && <p style={{ color: "red" }}>{errors.lastName.message}</p>}
            </div>
            <div>
                <input {...register("email")} placeholder="Email" />
                {errors.email && <p style={{ color: "red" }}>{errors.email.message}</p>}
            </div>
            <div>
                <input {...register("phoneNumber")} placeholder="Телефон" />
                {errors.phoneNumber && <p style={{ color: "red" }}>{errors.phoneNumber.message}</p>}
            </div>
            <div>
                <input type="date" {...register("birthDate")} />
                {errors.birthDate && <p style={{ color: "red" }}>{errors.birthDate.message}</p>}
            </div>

            <h4>Адреса</h4>

            <div>
                <input {...register("street")} placeholder="Вулиця" />
                {errors.street && <p style={{ color: "red" }}>{errors.street.message}</p>}
            </div>
            <div>
                <input {...register("houseNumber")} placeholder="Будинок" />
                {errors.houseNumber && <p style={{ color: "red" }}>{errors.houseNumber.message}</p>}
            </div>
            <div>
                <input {...register("city")} placeholder="Місто" />
                {errors.city && <p style={{ color: "red" }}>{errors.city.message}</p>}
            </div>
            <div>
                <input {...register("zipCode")} placeholder="Поштовий індекс" />
                {errors.zipCode && <p style={{ color: "red" }}>{errors.zipCode.message}</p>}
            </div>
            <div>
                <input {...register("country")} placeholder="Країна" />
                {errors.country && <p style={{ color: "red" }}>{errors.country.message}</p>}
            </div>

            <Button type="submit" loading={isSubmitting}>
                Зберегти зміни
            </Button>
        </form>
    )
}