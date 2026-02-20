import type {LoanApplicationRequest, LoanOfferResponse, LoanResponse} from "@/features/types/loan.ts";
import {api} from "@/api/api.ts";
import type {Account} from "@/features/types/account.ts";

export default class LoanService {
    static async getLoanOffers(data: LoanApplicationRequest): Promise<LoanOfferResponse[]>{
        const response = await api.post(`/accounts/loans/offers`,data)
        return response.data
    }

    static async approveLoan(data: LoanApplicationRequest): Promise<LoanResponse>{
        const response = await api.post(`/accounts/loans/approve`,data)
        return response.data
    }

    static async getMyLoans(): Promise<LoanResponse[]>{
        const response = await api.get(`/accounts/loans/my`)
        return response.data
    }

    static async getAvailableCreditAccounts(): Promise<Account[]>{
        const response = await api.get(`/accounts/loans/available-credit-accounts`)
        return response.data
    }
}