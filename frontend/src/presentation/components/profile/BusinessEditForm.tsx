import type {
    BusinessProfile,
    BusinessUserUpdateDto,
} from "@/domain/user/user-profile.types.ts"

import UserService from "@/application/user/user.service.ts"
import { useForm } from "react-hook-form"
import { z } from "zod"
import { zodResolver } from "@hookform/resolvers/zod"
import { Button } from "@/presentation/ui/button/Button.tsx"
import {BusinessCategory} from "@/domain/business/business-category.enum.ts";
import styles from "./UserEditForm.module.css";
import {Input} from "@/presentation/ui/input/Input.tsx";
import {Select} from "@/presentation/ui/select/Select.tsx";
import {Textarea} from "@/presentation/ui/textarea/Textarea.tsx";

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
            className={styles.form}
        >
            <div className={styles.grid}>

                <Input
                    label="Назва компанії"
                    error={errors.companyName?.message}
                    {...register("companyName")}
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
                    label="ЄДРПОУ"
                    error={errors.adrpou?.message}
                    {...register("adrpou")}
                />

                <div className={styles.fullWidth}>
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
                </div>

                <Textarea
                    label="Опис"
                    rows={4}
                    fullWidth
                    error={errors.description?.message}
                    {...register("description")}
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
