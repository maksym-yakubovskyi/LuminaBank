export const UserType = {
    INDIVIDUAL_USER: "INDIVIDUAL_USER",
    BUSINESS_USER: "BUSINESS_USER",
} as const

export type UserType = typeof UserType[keyof typeof UserType]

export const BusinessCategory = {
    COMMUNAL: "COMMUNAL",
    MOBILE: "MOBILE",
    INTERNET: "INTERNET",
    CREDIT: "CREDIT",
    FOOD: "FOOD",
    SHOPPING: "SHOPPING",
    SUBSTITUTIONS: "SUBSTITUTIONS",
    TRANSPORT: "TRANSPORT",
    MEDICINE: "MEDICINE",
    GAMING: "GAMING",
    SPORT: "SPORT",
    OTHER: "OTHER",
} as const

export type BusinessCategory = typeof BusinessCategory[keyof typeof BusinessCategory]

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

export const PaymentTemplateType = {
    SERVICE: "SERVICE",
    TRANSFER: "TRANSFER",
} as const

export type PaymentTemplateType = typeof PaymentTemplateType[keyof typeof PaymentTemplateType]

export const RecurrenceType = {
    NONE: "NONE",
    DAILY: "DAILY",
    WEKKLY: "WEEKLY",
    MONTHLY: "MONTHLY",
} as const

export type RecurrenceType = typeof RecurrenceType[keyof typeof RecurrenceType]

export const WeekDay = {
    MON: "MON",
    TUE: "TUE",
    WED: "WED",
    THU: "THU",
    FRI: "FRI",
    SAT: "SAT",
    SUN: "SUN",
} as const

export type WeekDay = typeof WeekDay[keyof typeof WeekDay]


export const ReportStatus = {
    PENDING: "PENDING",
    PROCESSING: "PROCESSING",
    READY: "READY",
    FAILED: "FAILED",
}
export type ReportStatus = typeof ReportStatus[keyof typeof ReportStatus]

export const ReportType = {
    MONTHLY_FINANCIAL: "MONTHLY_FINANCIAL",
    DAILY_ACTIVITY: "DAILY_ACTIVITY",
    TRANSACTION_HISTORY: "TRANSACTION_HISTORY",
}
export type ReportType = typeof ReportType[keyof typeof ReportType]