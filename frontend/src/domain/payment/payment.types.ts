export interface PaymentResponse{
    id: number
    fromCardNumber: string
    toCardNumber: string
    amount: number
    description: string
    status: string
    createdAt: string
}