export const Currency = {
    UAH: "UAH",
    USD: "USD",
    EUR: "EUR",
    GBP: "GBP",
    PLN: "PLN",
    CHF: "CHF",
} as const

export type Currency = typeof Currency[keyof typeof Currency]