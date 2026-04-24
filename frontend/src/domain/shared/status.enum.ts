export const Status = {
    ACTIVE: "ACTIVE",
    INACTIVE: "INACTIVE",
    BLOCKED: "BLOCKED",
} as const

export type Status = typeof Status[keyof typeof Status]