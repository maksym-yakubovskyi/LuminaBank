export const PaymentStatus = {
    RISK_PENDING: "RISK_PENDING",
    PENDING: "PENDING",
    PROCESSING: "PROCESSING",
    SUCCESS: "SUCCESS",
    FAILED: "FAILED",
    CANCELLED: "CANCELLED",
    FLAGGED: "FLAGGED",
    BLOCKED: "BLOCKED",
    REJECTED: "REJECTED",
} as const

export type PaymentStatus =
    typeof PaymentStatus[keyof typeof PaymentStatus]