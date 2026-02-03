export interface AnalyticsTopRecipientResponse{
    recipientId: number
    displayName: string
    totalAmount: number
    transactionCount: number
}

export interface AnalyticsMonthlyOverviewResponse{
    month: string
    totalIncome: number
    totalExpenses: number
    cashFlow: number
    transactionCount: number
}

export interface AnalyticsCategoryResponse{
    category: string
    totalAmount: number
    percentage: number
}