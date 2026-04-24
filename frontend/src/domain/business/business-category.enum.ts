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