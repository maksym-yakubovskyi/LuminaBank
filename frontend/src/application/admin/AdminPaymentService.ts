import {api} from "@/infrastructure/http/api-client.ts";

export default class AdminPaymentService {

    static async approve(paymentId: number) {
        await api.post(`/payments/admin/approve/${paymentId}`)
    }

    static async reject(paymentId: number) {
        await api.post(`/payments/admin/reject/${paymentId}`)
    }
}