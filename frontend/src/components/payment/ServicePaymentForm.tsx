import { useEffect, useState } from "react";
import { z } from "zod";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import type {Card} from "@/features/types/card.ts";
import type {Provider} from "@/features/types/provider.ts";
import {Button} from "@/components/button/Button.tsx";
import BusinessUserService from "@/api/service/BusinessUserService.ts";
import {recurrenceSchema} from "@/features/types/template.ts";
import {RecurrenceFields} from "@/components/payment/RecurrenceFields.tsx";
import {BusinessCategory, Status} from "@/features/enum/enum.ts";

const servicePaymentSchema = z.object({
    fromCardNumber: z.string().nonempty("Оберіть карту для списання"),

    category: z.enum(BusinessCategory, {
        message: "Оберіть категорію",
    }),

    providerId: z.number( "Оберіть провайдера" )
        .int("Некоректний провайдер")
        .positive("Оберіть провайдера"),

    payerReference: z.string().nonempty("Вкажіть номер телефону / особовий рахунок")
        .min(3, "Мінімум 3 символи")
        .max(64, "Максимум 64 символи"),

    amount: z.number("Сума обов'язкова" )
        .min(0.01, "Сума повинна бути більше 0"),

    description: z.string().max(255, "Максимум 255 символів").optional(),

    saveAsTemplate: z.boolean(),
    templateName: z.string().optional(),

    recurrence: recurrenceSchema,
}).superRefine((val, ctx) => {
    if (val.saveAsTemplate) {
        if (!val.templateName || val.templateName.trim().length < 3) {
            ctx.addIssue({
                code: "custom",
                path: ["templateName"],
                message: "Назва шаблону мінімум 3 символи",
            })
        }
    }
})

export type ServicePaymentFormInputs = z.infer<typeof servicePaymentSchema>;

interface Props {
    cards: Card[]
    loading: boolean
    onSubmit: (data: ServicePaymentFormInputs) => Promise<void> | void
}

export function ServicePaymentForm({ cards,loading, onSubmit}: Props){
    const [providers, setProviders] = useState<Provider[]>([])

    const {
        register,
        handleSubmit,
        watch,
        setValue,
        resetField,
        formState: { errors, isSubmitting },
    } = useForm<ServicePaymentFormInputs>({
        resolver: zodResolver(servicePaymentSchema),
        defaultValues: {
            fromCardNumber: cards?.[0]?.cardNumber ?? "",
            category: BusinessCategory.MOBILE,
            providerId: 0,
            payerReference: "",
            amount: 0,
            description: "",

            saveAsTemplate: false,
            templateName: "",

            recurrence: {
                enabled: false,
                type: undefined,
                hour: 10,
                minute: 0,
                dayOfWeek: undefined,
                dayOfMonth: undefined,
            },
        },
    })

    const category = watch("category")
    const saveAsTemplate = watch("saveAsTemplate")

    useEffect(() => {
        void loadProviders(category)
    }, [])

    async function loadProviders(category: BusinessCategory) {
        try {
            const data = await BusinessUserService.getProviders(category)
            setProviders(data)
            resetField("providerId")
            setValue("providerId", 0)
        } catch (e) {
            console.error("Providers load failed", e)
            setProviders([])
        }
    }

    if (loading) return <>Завантаження...</>

    return (
        <form onSubmit={handleSubmit(onSubmit)}>

            <div style={{ marginBottom: "12px" }}>
                <label>З якої карти списати</label>
                <select {...register("fromCardNumber")} style={{ display: "block", width: "100%" }}>
                    <option value="" disabled>
                        Виберіть карту
                    </option>

                    {cards
                        .filter((c) => c.status === Status.ACTIVE)
                        .map((card) => (
                            <option key={card.id} value={card.cardNumber}>
                                {card.cardNumber} ({card.cardNetwork})
                            </option>
                        ))}
                </select>

                {errors.fromCardNumber && (
                    <p style={{ color: "red" }}>{errors.fromCardNumber.message}</p>
                )}

                {cards.length === 0 && (
                    <p style={{ color: "gray" }}>У вас поки немає карток</p>
                )}
            </div>

            <div style={{ marginBottom: "12px" }}>
                <label>Категорія</label>
                <select {...register("category")} style={{ display: "block", width: "100%" }}>
                    <option value="MOBILE">Мобільний зв’язок</option>
                    <option value="COMMUNAL">Комунальні</option>
                    <option value="INTERNET">Інтернет</option>
                    <option value="CREDIT">Кредити</option>
                    <option value="OTHER">Інше</option>
                </select>

                {errors.category && <p style={{ color: "red" }}>{errors.category.message}</p>}
            </div>

            <div style={{ marginBottom: "12px" }}>
                <label>Провайдер</label>

                <select
                    {...register("providerId", { valueAsNumber: true })}
                    style={{ display: "block", width: "100%" }}
                >
                    <option value={0} disabled>
                        Виберіть провайдера
                    </option>

                    {providers.map((p) => (
                        <option key={p.id} value={p.id}>
                            {p.companyName}
                        </option>
                    ))}
                </select>

                {errors.providerId && <p style={{ color: "red" }}>{errors.providerId.message}</p>}

                {providers.length === 0 && (
                    <p style={{ color: "gray" }}>Немає провайдерів в цій категорії</p>
                )}
            </div>

            <div style={{ marginBottom: "12px" }}>
                <label>Телефон / Особовий рахунок</label>
                <input
                    type="text"
                    placeholder="Напр. 380991234567 або 12345678"
                    {...register("payerReference")}
                    style={{ display: "block", width: "100%" }}
                />
                {errors.payerReference && (
                    <p style={{ color: "red" }}>{errors.payerReference.message}</p>
                )}
            </div>

            <div style={{ marginBottom: "12px" }}>
                <label>Сума</label>
                <input
                    type="number"
                    step="0.01"
                    {...register("amount", { valueAsNumber: true })}
                    style={{ display: "block", width: "100%" }}
                />
                {errors.amount && <p style={{ color: "red" }}>{errors.amount.message}</p>}
            </div>

            <div style={{ marginBottom: "12px" }}>
                <label>Коментар</label>
                <input
                    type="text"
                    placeholder="Необов'язково"
                    {...register("description")}
                    style={{ display: "block", width: "100%" }}
                />
                {errors.description && <p style={{ color: "red" }}>{errors.description.message}</p>}
            </div>

            {/* Зберегти як шаблон */}
            <div style={{ marginTop: "16px", paddingTop: "12px", borderTop: "1px solid #eee" }}>
                <label style={{ display: "flex", gap: "8px", alignItems: "center" }}>
                    <input type="checkbox" {...register("saveAsTemplate")} />
                    Зберегти як шаблон
                </label>

                {saveAsTemplate && (
                    <>
                        <div style={{ marginTop: "12px" }}>
                            <label>Назва шаблону</label>
                            <input
                                type="text"
                                placeholder="Напр: Оплата Київстар"
                                {...register("templateName")}
                                style={{ display: "block", width: "100%" }}
                            />
                            {errors.templateName && <p style={{ color: "red" }}>{errors.templateName.message}</p>}
                        </div>

                        {/* Регулярність */}
                        <RecurrenceFields
                            register={register}
                            watch={watch}
                            errors={errors}
                            path="recurrence" />
                    </>
                )}
            </div>

            <Button type="submit" loading={isSubmitting} disabled={cards.length === 0 || providers.length === 0}>
                {saveAsTemplate ? "Створити шаблон" : "Оплатити"}
            </Button>
        </form>
    )
}