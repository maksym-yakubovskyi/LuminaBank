export const RecurrenceType = {
    NONE: "NONE",
    DAILY: "DAILY",
    WEKKLY: "WEEKLY",
    MONTHLY: "MONTHLY",
} as const

export type RecurrenceType = typeof RecurrenceType[keyof typeof RecurrenceType]