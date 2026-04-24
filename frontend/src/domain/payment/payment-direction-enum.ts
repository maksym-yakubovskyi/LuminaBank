export const PaymentDirection = {
    OUTGOING: "OUTGOING",
    INCOMING: "INCOMING",
    INTERNAL: "INTERNAL",
} as const

export type PaymentDirection =
    typeof PaymentDirection[keyof typeof PaymentDirection]