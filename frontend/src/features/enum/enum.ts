export const UserType = {
    INDIVIDUAL_USER: "INDIVIDUAL_USER",
    BUSINESS_USER: "BUSINESS_USER",
} as const

export type UserType = typeof UserType[keyof typeof UserType]

export const AccountType = {
    CREDIT: "CREDIT",
    DEBIT: "DEBIT",
} as const

export type AccountType = typeof AccountType[keyof typeof AccountType]

export const Currency = {
    UAH: "UAH",
    USD: "USD",
    EUR: "EUR",
    GBP: "GBP",
    PLN: "PLN",
    CHF: "CHF",
} as const

export type Currency = typeof Currency[keyof typeof Currency]

export const Status = {
    ACTIVE: "ACTIVE",
    INACTIVE: "INACTIVE",
    BLOCKED: "BLOCKED",
} as const

export type Status = typeof Status[keyof typeof Status]

export const CardType = {
    PHYSICAL: "PHYSICAL",
    VIRTUAL: "VIRTUAL",
} as const

export type CardType = typeof CardType[keyof typeof CardType]

export const CardNetwork = {
    VISA: "VISA",
    MASTERCARD: "MASTERCARD",
} as const

export type CardNetwork = typeof CardNetwork[keyof typeof CardNetwork]
