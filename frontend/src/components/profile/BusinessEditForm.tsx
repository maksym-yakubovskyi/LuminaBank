import type {
    BusinessProfile,
    BusinessUserUpdateDto,
} from "@/features/types/userProfile.ts"

import UserService from "@/api/service/UserService.ts"
import { useForm } from "react-hook-form"
import { z } from "zod"
import { zodResolver } from "@hookform/resolvers/zod"
import { Button } from "@/components/button/Button.tsx"
import { BusinessCategory } from "@/features/enum/enum.ts"

const businessEditSchema = z.object({
    companyName: z.string().nonempty("Назва компанії обов'язкова")
        .min(2, "Назва компанії мінімум 2 символи")
        .max(150, "Назва максимум 150 символів"),

    email: z.email({ message: "Невірний email" }),

    phoneNumber: z.string().nonempty("Телефон обов'язковий")
        .regex(/^\+?[0-9\s()-]+$/, "Невірний формат телефону"),

    adrpou: z.string()
        .min(5, "ЄДРПОУ мінімум 5 символів")
        .max(20, "ЄДРПОУ максимум 20 символів"),

    description: z.string().optional(),

    category: z.enum(BusinessCategory,{ message: "Оберіть категорію" }),

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

type FormInputs = z.infer<typeof businessEditSchema>

interface Props {
    business: BusinessProfile
    onUpdate: (profile: BusinessProfile) => void
}

export function BusinessEditForm({ business, onUpdate }: Props) {
    const {
        register,
        handleSubmit,
        formState: { errors, isSubmitting },
    } = useForm<FormInputs>({
        resolver: zodResolver(businessEditSchema),
        values: {
            companyName: business.companyName,
            email: business.email,
            phoneNumber: business.phoneNumber,
            adrpou: business.adrpou,
            description: business.description ?? "",
            category: business.category,

            street: business.address?.street ?? "",
            city: business.address?.city ?? "",
            houseNumber: business.address?.houseNumber ?? "",
            zipCode: business.address?.zipCode ?? "",
            country: business.address?.country ?? "",
        },
    })

    const onSubmit = async (data: FormInputs) => {
        try {
            const updated = await UserService.updateBusinessProfile({
                companyName: data.companyName,
                email: data.email,
                phoneNumber: data.phoneNumber,
                adrpou: data.adrpou,
                description: data.description ?? "",
                category: data.category,

                street: data.street,
                city: data.city,
                houseNumber: data.houseNumber,
                zipCode: data.zipCode,
                country: data.country,
            } as BusinessUserUpdateDto)

            onUpdate(updated)
        } catch (e) {
            console.error("Update business profile failed", e)
            alert("Помилка оновлення")
        }
    }

    return (
        <form
            onSubmit={handleSubmit(onSubmit)}
            style={{ display: "grid", gap: "12px" }}
        >
            <h4>Редагування бізнес-профілю</h4>

            <div>
                <input {...register("companyName")} placeholder="Назва компанії" />
                {errors.companyName && <p style={{ color: "red" }}>{errors.companyName.message}</p>}
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
                <input {...register("adrpou")} placeholder="ЄДРПОУ" />
                {errors.adrpou && <p style={{ color: "red" }}>{errors.adrpou.message}</p>}
            </div>

            <div>
                <textarea {...register("description")} placeholder="Опис компанії" />
            </div>

            <div>
                <select {...register("category")}>
                    <option value="">Оберіть категорію</option>
                    {Object.values(BusinessCategory).map((cat) => (
                        <option key={cat} value={cat}>
                            {cat}
                        </option>
                    ))}
                </select>
                {errors.category && (
                    <p style={{ color: "red" }}>{errors.category.message}</p>
                )}
            </div>

            <h4>Адреса</h4>

            <div>
                <input {...register("street")} placeholder="Вулиця" />
                {errors.street && <p style={{ color: "red" }}>{errors.street.message}</p>}
            </div>

            <div>
                <input {...register("houseNumber")} placeholder="Будинок" />
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
