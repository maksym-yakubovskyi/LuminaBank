export const ReportStatus = {
    PENDING: "PENDING",
    PROCESSING: "PROCESSING",
    READY: "READY",
    FAILED: "FAILED",
}
export type ReportStatus = typeof ReportStatus[keyof typeof ReportStatus]