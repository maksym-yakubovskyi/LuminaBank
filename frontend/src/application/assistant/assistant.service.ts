import {api} from "@/infrastructure/http/api-client.ts";
import type {ChatRequest, ChatResponse, ConversationResponse} from "@/domain/assistant/assistant.types.ts";

export default class AssistantService {
    static async sendMessage(message: string, conversationId?: string): Promise<ChatResponse> {
        const payload: ChatRequest = { message }

        if (conversationId) {
            payload.conversationId = conversationId
        }

        const response = await api.post(`/assistant/chat`,payload)
        return response.data
    }

    static async loadConversations(): Promise<ConversationResponse[]>{
        const response = await api.get(`/assistant/conversations`)
        return response.data
    }

    static async loadMessages(conversationId: string) {
        const response = await api.get(`/assistant/conversations/${conversationId}/messages`)
        return response.data
    }

    static async deleteConversation(id: string): Promise<void> {
        await api.delete(`/assistant/conversations/${id}`)
    }
}