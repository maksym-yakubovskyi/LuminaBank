import {z} from "zod";
import {useForm} from "react-hook-form";
import {zodResolver} from "@hookform/resolvers/zod";
import {Button} from "@/components/button/Button.tsx";
import type {Card} from "@/features/types/card.ts";
import {recurrenceSchema} from "@/features/types/template.ts";
import {RecurrenceFields} from "@/components/payment/RecurrenceFields.tsx";
import {Status} from "@/features/enum/enum.ts";

const cardTransferSchema = z.object({
    fromCardNumber: z.string().nonempty("Оберіть карту для списання"),

    toCardNumber: z.string().nonempty("Номер карти отримувача обов'язковий")
        .regex(/^\d{16}$/, "Номер карти має містити 16 цифр"),

    amount: z.number("Сума обов'язкова")
        .min(0.01, "Сума повинна бути більше 0"),

    description: z.string().max(255, "Призначення максимум 255 символів")
        .optional(),

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

export type CardTransferFormInputs = z.infer<typeof cardTransferSchema>

interface Props {
    cards: Card[]
    loading: boolean
    onSubmit: (data: CardTransferFormInputs) => Promise<void> | void
}

export function CardTransferForm({ cards,loading,  onSubmit }: Props) {
    const {
        register,
        handleSubmit,
        watch,
        formState: { errors, isSubmitting },
    } = useForm<CardTransferFormInputs>({
        resolver: zodResolver(cardTransferSchema),
        defaultValues: {
            fromCardNumber: cards?.[0]?.cardNumber ?? "",
            toCardNumber: "",
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

    const saveAsTemplate = watch("saveAsTemplate")

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
                <label>Номер карти отримувача</label>
                <input
                    type="text"
                    placeholder="4444333322221111"
                    {...register("toCardNumber")}
                    style={{ display: "block", width: "100%" }}
                />
                {errors.toCardNumber && (
                    <p style={{ color: "red" }}>{errors.toCardNumber.message}</p>
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
                <label>Призначення платежу</label>
                <input
                    type="text"
                    placeholder="Наприклад: за ремонт"
                    {...register("description")}
                    style={{ display: "block", width: "100%" }}
                />
                {errors.description && (
                    <p style={{ color: "red" }}>{errors.description.message}</p>
                )}
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
                                placeholder="Напр: Переказ мамі"
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
                            path="recurrence"
                        />
                    </>
                )}
            </div>

            <Button type="submit" loading={isSubmitting} disabled={cards.length === 0}>
                {saveAsTemplate ? "Створити шаблон" : "Переказати"}
            </Button>
        </form>
    )
}