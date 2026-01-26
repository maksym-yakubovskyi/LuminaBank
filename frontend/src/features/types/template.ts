import {z} from "zod";
import type {PaymentTemplateType} from "@/features/enum/enum.ts";
import {RecurrenceType,WeekDay} from "@/features/enum/enum.ts";

export interface CreateTemplateRequest {
    name: string
    fromCardNumber: string
    type: PaymentTemplateType

    // transfer
    toCardNumber?: string

    // service
    providerId?: number
    payerReference?: string

    amount: number
    description?: string

    recurrenceType: RecurrenceType
    hour?: number | null
    minute?: number | null
    dayOfWeek?: WeekDay | null
    dayOfMonth?: number | null
}

export interface TemplateItem{
    id: number
    userId: number
    type: PaymentTemplateType
    name: string
    description?: string
    fromCardNumber: string
    toCardNumber: string
    amount: number
    isRecurring: boolean
    nextExecutionTime: string | null
}

export const recurrenceSchema = z.object({
    enabled: z.boolean(),

    type: z.enum([RecurrenceType.DAILY, RecurrenceType.WEKKLY, RecurrenceType.MONTHLY]).optional(),

    hour: z.number().min(0).max(23).optional(),
    minute: z.number().min(0).max(59).optional(),

    dayOfWeek: z.enum([WeekDay.MON, WeekDay.TUE,WeekDay.WED,
        WeekDay.THU,WeekDay.FRI,WeekDay.SAT,WeekDay.SUN]).optional(),
    dayOfMonth: z.number().min(1).max(28).optional(),
}).superRefine((val, ctx) => {
    if (!val.enabled) return;

    if (!val.type) {
        ctx.addIssue({ code: "custom", path: ["type"], message: "Оберіть тип регулярності" })
        return;
    }

    if (val.hour === undefined || val.minute === undefined) {
        ctx.addIssue({ code: "custom", path: ["hour"], message: "Оберіть час" })
    }

    if (val.type === RecurrenceType.WEKKLY && !val.dayOfWeek) {
        ctx.addIssue({ code: "custom", path: ["dayOfWeek"], message: "Оберіть день тижня" })
    }

    if (val.type === RecurrenceType.MONTHLY && !val.dayOfMonth) {
        ctx.addIssue({ code: "custom", path: ["dayOfMonth"], message: "Оберіть день місяця" })
    }
})