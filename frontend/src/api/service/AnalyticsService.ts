import type {
    AnalyticsCategoryResponse,
    AnalyticsMonthlyOverviewResponse,
    AnalyticsTopRecipientResponse, ReportResponse
} from "@/features/types/analytics.ts";
import {api} from "@/api/api.ts";

export default class AnalyticsService{
    static async getOverview(accountId: number,month?: string): Promise<AnalyticsMonthlyOverviewResponse>{
        const response = await api.get("/analytics/overview", {
            params: {
                accountId,
                month
            }
        })
        return response.data
    }

    static async getCategoriesAnalytics(month?: string): Promise<AnalyticsCategoryResponse[]>{
        const response = await api.get("/analytics/categories", {
            params: { month }
        })
        return response.data
    }

    static async getTopRecipients(): Promise<AnalyticsTopRecipientResponse[]>{
        const response = await api.get(`/analytics/top-recipients`)
        return response.data
    }

    static async getMyReports(): Promise<ReportResponse[]>{
        const response = await api.get(`/analytics/reports/my`)
        return response.data
    }

    static async getReportStatus(id: string){
        const response = await api.get(`/analytics/reports/${id}/status`)
        return response.data
    }

    static async createMonthlyReport(accountId: number,month: string){
        const data = {
            accountId: accountId,
            month: month,
        }

        const response = await api.post("/analytics/reports/monthly",data)
        return response.data
    }

    static async createDailyReport(from: string, to: string){
        const data = {
            from: from,
            to: to,
        }

        const response = await api.post("/analytics/reports/daily",data)
        return response.data
    }

    static async createTransactionHistoryReport(from: string, to: string){
        const data = {
            from: from,
            to: to,
        }

        const response = await api.post("/analytics/reports/transaction-history",data)
        return response.data
    }

    static async downloadReport(id: string){
        const response = await api.get(
            `/analytics/reports/${id}/download`,
            {
                responseType: "blob"
            }
        )
        const contentDisposition = response.headers["content-disposition"]
        let fileName = "undefined"

        if (contentDisposition) {
            const match = contentDisposition.match(/filename="([^"]+)"/)
            if (match?.[1]) {
                fileName = match[1]
            }
        }

        const blob = new Blob([response.data], {
            type: response.headers["content-type"]
        })

        const url = window.URL.createObjectURL(blob)
        const link = document.createElement("a")
        link.href = url
        link.download = fileName
        document.body.appendChild(link)
        link.click()

        link.remove()
        window.URL.revokeObjectURL(url)
    }
}