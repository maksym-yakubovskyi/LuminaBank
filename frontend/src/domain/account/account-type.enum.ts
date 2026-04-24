export const AccountType = {
    CREDIT: "CREDIT",
    DEBIT: "DEBIT",
    MERCHANT: "MERCHANT",
} as const

export type AccountType = typeof AccountType[keyof typeof AccountType]