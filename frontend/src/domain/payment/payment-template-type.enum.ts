export const PaymentTemplateType = {
    SERVICE: "SERVICE",
    TRANSFER: "TRANSFER",
} as const

export type PaymentTemplateType = typeof PaymentTemplateType[keyof typeof PaymentTemplateType]