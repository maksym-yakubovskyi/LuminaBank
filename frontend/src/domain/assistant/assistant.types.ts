import type {MessageSender} from "@/domain/assistant/assistant-sender.enum.ts";
import type {ConversationStatus} from "@/domain/assistant/conversation-status.enum.ts";

export interface ChatRequest {
    message: string
    conversationId?: string
}

export interface ChatResponse {
    type: string
    message: string
    conversationId: string
}

export interface ChatMessageResponse {
    id: string
    sender: MessageSender
    content: string
    createdAt: string
}

export interface ChatMessageUI {
    id: string
    content: string
    sender: MessageSender
}

export interface ConversationResponse {
    id: string
    status: ConversationStatus
    createdAt: string
    lastMessageAt: string
}