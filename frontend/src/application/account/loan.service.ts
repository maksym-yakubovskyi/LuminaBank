import type {LoanApplicationRequest, LoanOfferResponse, LoanResponse} from "@/domain/loan/loan.types.ts";
import {api} from "@/infrastructure/http/api-client.ts";
import type {Account} from "@/domain/account/account.types.ts";

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