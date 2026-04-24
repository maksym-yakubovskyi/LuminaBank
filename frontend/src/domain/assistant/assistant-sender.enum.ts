export const MessageSender = {
    USER: "USER",
    ASSISTANT: "ASSISTANT",
} as const

export type MessageSender = typeof MessageSender[keyof typeof MessageSender]