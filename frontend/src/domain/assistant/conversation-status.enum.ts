export const ConversationStatus = {
    ACTIVE: "ACTIVE",
    CLOSED: "CLOSED",
} as const
export type ConversationStatus = typeof ConversationStatus[keyof typeof ConversationStatus];