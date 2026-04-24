import {api} from "@/infrastructure/http/api-client.ts";
import type {CreateTemplateRequest, TemplateItem} from "@/domain/payment/template.types.ts";
import type {PaymentResponse} from "@/domain/payment/payment.types.ts";

export default class PaymentService {
    static async makePayment(data: {
        fromCardNumber: string
        toCardNumber: string
        amount: number
        description?: string }): Promise<PaymentResponse> {
        const response = await api.post(`/payments`, data)
        return response.data
    }

    static async makePaymentService(data: {
        fromCardNumber: string
        providerId: number
        category: string
        payerReference: string
        amount: number
        description?: string }): Promise<PaymentResponse> {
        const response = await api.post(`/payments/service`, data)
        return response.data
    }

    static async makeTemplatePayment(id: number): Promise<void> {
        await api.post(`/payments/template/${id}`)
    }

    static async cancelPayment(id: number): Promise<void> {
        await api.delete(`/payments/${id}/cancel`)
    }

    static async createPaymentTemplate(data: CreateTemplateRequest): Promise<void> {
        await api.post(`/payments/payment_templates`, data)
    }

    static async getMyPaymentTemplates(): Promise<TemplateItem[]>{
        const response = await api.get(`/payments/payment_templates/my`)
        return response.data
    }

    static async deletePaymentTemplate(id: number): Promise<void> {
        await api.delete(`/payments/payment_templates/${id}`)
    }
}