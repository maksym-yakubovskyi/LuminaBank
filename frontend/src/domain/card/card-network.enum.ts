export const CardNetwork = {
    VISA: "VISA",
    MASTERCARD: "MASTERCARD",
} as const

export type CardNetwork = typeof CardNetwork[keyof typeof CardNetwork]