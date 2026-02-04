import type {
    AnalyticsCategoryResponse,
    AnalyticsMonthlyOverviewResponse,
    AnalyticsTopRecipientResponse
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
}