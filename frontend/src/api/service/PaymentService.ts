import {api} from "@/api/api.ts";
import type {CreateTemplateRequest, TemplateItem} from "@/features/types/template.ts";

export default class PaymentService {
    static async makePayment(data: {
        fromCardNumber: string
        toCardNumber: string
        amount: number
        description?: string }): Promise<void> {
        await api.post(`/payments`, data);
    }

    static async makePaymentService(data: {
        fromCardNumber: string
        providerId: number
        payerReference: string
        amount: number
        description?: string }): Promise<void> {
        await api.post(`/payments/service`, data);
    }

    static async makeTemplatePayment(id: number): Promise<void> {
        await api.post(`/payments/template/${id}`);
    }

    static async createPaymentTemplate(data: CreateTemplateRequest): Promise<void> {
        await api.post(`/payments/payment_templates`, data);
    }

    static async getMyPaymentTemplates(): Promise<TemplateItem[]>{
        const response = await api.get(`/payments/payment_templates/my`)
        return response.data
    }

    static async deletePaymentTemplate(id: number): Promise<boolean> {
        try{
            const response = await api.delete(`/payments/payment_templates/${id}`);
            return response.status === 204;
        }catch(error){
            return false;
        }
    }
}