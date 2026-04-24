export const ReportType = {
    MONTHLY_FINANCIAL: "MONTHLY_FINANCIAL",
    DAILY_ACTIVITY: "DAILY_ACTIVITY",
    TRANSACTION_HISTORY: "TRANSACTION_HISTORY",
}
export type ReportType = typeof ReportType[keyof typeof ReportType]