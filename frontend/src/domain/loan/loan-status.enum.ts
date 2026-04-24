export const LoanStatus = {
    ACTIVE: "ACTIVE",       // Кредит активний (гроші зараховані)
    CLOSED: "CLOSED",       // Повністю погашений
    OVERDUE: "OVERDUE",     // Є прострочені платежі
} as const

export type LoanStatus = typeof LoanStatus[keyof typeof LoanStatus]

export const InstallmentStatus = {
    PENDING: "PENDING",               // Очікує настання дати платежу
    DUE: "DUE",                       // Термін платежу настав
    PARTIALLY_PAID: "PARTIALLY_PAID", // Частково сплачений
    PAID: "PAID",                     // Повністю сплачений
    OVERDUE: "OVERDUE",               // Прострочений
    CLOSED: "CLOSED",                 // Списаний / закритий
} as const

export type InstallmentStatus =
    typeof InstallmentStatus[keyof typeof InstallmentStatus]