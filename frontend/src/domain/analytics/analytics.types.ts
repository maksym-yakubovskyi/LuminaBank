import type {ReportType} from "@/domain/analytics/report-type.enum.ts";
import type {ReportStatus} from "@/domain/analytics/report-status.enum.ts";


export interface AnalyticsTopRecipientResponse{
    recipientId: number
    displayName: string
    totalAmount: number
    transactionCount: number
}

export interface AnalyticsMonthlyOverviewResponse{
    month: string
    totalIncome: number
    totalExpense: number
    cashFlow: number
    transactionCount: number
}

export interface AnalyticsCategoryResponse{
    category: string
    totalAmount: number
    percentage: number
}

export interface ReportResponse{
    id: string
    reportType: ReportType
    createdAt: string
    status: ReportStatus
}