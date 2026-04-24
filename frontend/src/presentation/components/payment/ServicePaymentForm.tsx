import { useEffect, useState } from "react";
import { z } from "zod";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import type {Card} from "@/domain/card/card.types.ts";
import type {Provider} from "@/domain/business/provider.types.ts";
import {Button} from "@/presentation/ui/button/Button.tsx";
import BusinessUserService from "@/application/user/business.service.ts";
import {recurrenceSchema} from "@/domain/payment/template.types.ts";
import {RecurrenceFields} from "@/presentation/components/payment/RecurrenceFields.tsx";
import {BusinessCategory} from "@/domain/business/business-category.enum.ts";
import {Status} from "@/domain/shared/status.enum.ts";
import styles from "./PaymentForm.module.css"
import {StateMessage} from "@/presentation/ui/state_message/StateMessage.tsx";
import { Select } from "@/presentation/ui/select/Select";
import {Input} from "@/presentation/ui/input/Input.tsx";

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

    if (loading)
        return <StateMessage>Завантаження...</StateMessage>

    return (
        <form
            onSubmit={handleSubmit(onSubmit)}
            className={styles.form}
        >

            <Select
                label="З якої карти списати"
                {...register("fromCardNumber")}
                error={errors.fromCardNumber?.message}
            >
                <option value="">Виберіть карту</option>
                {cards
                    .filter(c => c.status === Status.ACTIVE)
                    .map(card => (
                        <option key={card.id} value={card.cardNumber}>
                            **** {card.cardNumber.slice(-4)} ({card.cardNetwork})
                        </option>
                    ))}
            </Select>

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

            <Select
                label="Провайдер"
                {...register("providerId", { valueAsNumber: true })}
                error={errors.providerId?.message}
            >
                <option value={0}>
                    Виберіть провайдера
                </option>

                {providers.map(p => (
                    <option key={p.id} value={p.id}>
                        {p.companyName}
                    </option>
                ))}
            </Select>

            <Input
                label="Реквізити"
                placeholder="380991234567 або 12345678"
                {...register("payerReference")}
                error={errors.payerReference?.message}
            />

            <Input
                type="number"
                step="0.01"
                min="0.01"
                label="Сума"
                {...register("amount", { valueAsNumber: true })}
                error={errors.amount?.message}
            />

            <Input
                label="Коментар"
                placeholder="Необов'язково"
                {...register("description")}
                error={errors.description?.message}
            />

            <div className={styles.sectionDivider}>

                <div className={styles.checkboxRow}>
                    <input
                        type="checkbox"
                        {...register("saveAsTemplate")}
                    />
                    <span>Зберегти як шаблон</span>
                </div>

                {saveAsTemplate && (
                    <>
                        <Input
                            label="Назва шаблону"
                            placeholder="Напр: Оплата Київстар"
                            {...register("templateName")}
                            error={errors.templateName?.message}
                        />

                        <RecurrenceFields
                            register={register}
                            watch={watch}
                            errors={errors}
                            path="recurrence"
                        />
                    </>
                )}

            </div>


            <Button
                type="submit"
                loading={isSubmitting}
                disabled={
                    cards.length === 0 ||
                    providers.length === 0
                }
            >
                {saveAsTemplate
                    ? "Створити шаблон"
                    : "Оплатити"}
            </Button>
        </form>
    )
}