import {z} from "zod";
import {useForm} from "react-hook-form";
import {zodResolver} from "@hookform/resolvers/zod";
import {Button} from "@/presentation/ui/button/Button.tsx";
import type {Card} from "@/domain/card/card.types.ts";
import {recurrenceSchema} from "@/domain/payment/template.types.ts";
import {RecurrenceFields} from "@/presentation/components/payment/RecurrenceFields.tsx";
import {Status} from "@/domain/shared/status.enum.ts";
import styles from "./PaymentForm.module.css"
import {StateMessage} from "@/presentation/ui/state_message/StateMessage.tsx";
import { Select } from "@/presentation/ui/select/Select";
import {Input} from "@/presentation/ui/input/Input.tsx";

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

            {/*{cards.length === 0 && (*/}
            {/*    <p style={{ color: "gray" }}>У вас поки немає карток</p>*/}
            {/*)}*/}

            <Input
                label="Номер карти отримувача"
                placeholder="4444333322221111"
                {...register("toCardNumber")}
                error={errors.toCardNumber?.message}
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
                label="Призначення платежу"
                placeholder="Наприклад: за ремонт"
                {...register("description")}
                error={errors.description?.message}
            />

            <div className={styles.sectionDivider}>

                <div className={styles.checkboxRow}>
                    <input type="checkbox" {...register("saveAsTemplate")} />
                    <span>Зберегти як шаблон</span>
                </div>

                {saveAsTemplate && (
                    <>
                        <Input
                            label="Назва шаблону"
                            placeholder="Напр: Переказ мамі"
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
                disabled={cards.length === 0}
            >
                {saveAsTemplate ? "Створити шаблон" : "Переказати"}
            </Button>
        </form>
    )
}