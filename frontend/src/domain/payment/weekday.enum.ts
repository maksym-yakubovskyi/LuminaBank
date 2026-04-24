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