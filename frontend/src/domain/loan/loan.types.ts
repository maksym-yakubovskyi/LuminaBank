import type {InstallmentStatus, LoanStatus} from "@/domain/loan/loan-status.enum.ts";

export interface LoanApplicationRequest {
    creditAccountId: number
    requestedAmount: number
    requestedTermMonths: number
}

export interface LoanOfferResponse{
    approvedAmount: number
    interestRate: number
    termMonths: number
    monthlyPayment: number
    totalPayable: number
    riskScore: number
    approved: boolean
}

export interface LoanInstallmentResponse{
    id: number
    installmentNumber: number
    dueDate: string
    principalPart: number
    interestPart: number
    totalAmount: number
    paidAmount: number
    penaltyAmount: number
    remainingAmount: number
    paidAt:string
    status:InstallmentStatus
}

export interface LoanResponse{
    id: number
    creditAccountId: number
    principalAmount: number
    interestRate: number
    termMonths: number
    monthlyPayment: number
    remainingPrincipal: number
    totalInterestAmount: number
    totalPayableAmount: number
    status: LoanStatus
    riskScore: number
    approvedAt: string
    closedAt: string
    createdAt: string
    totalInstallments: number
    paidInstallments: number
    overdueInstallments: number
    installments: LoanInstallmentResponse[]
}