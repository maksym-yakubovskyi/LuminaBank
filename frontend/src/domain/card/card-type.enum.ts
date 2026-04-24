export const CardType = {
    PHYSICAL: "PHYSICAL",
    VIRTUAL: "VIRTUAL",
} as const

export type CardType = typeof CardType[keyof typeof CardType]