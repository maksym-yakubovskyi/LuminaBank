import {useEffect, useRef, useState} from "react"
import type {ChatMessageUI, ChatMessageResponse, ConversationResponse} from "@/domain/assistant/assistant.types.ts"
import AssistantService from "@/application/assistant/assistant.service.ts"
import styles from "./ChatBlock.module.css"
import {MessageSender} from "@/domain/assistant/assistant-sender.enum.ts";
import {MessageInput} from "@/presentation/components/dashboard/chat/MessageInput.tsx";
import {ChatSidebar} from "@/presentation/components/dashboard/chat/ChatSidebar.tsx";
import {ConversationStatus} from "@/domain/assistant/conversation-status.enum.ts";
import type {BlockState} from "@/domain/shared/block-state.type.ts";

export function ChatBlock() {
    const [conversationId, setConversationId] = useState<string | null>(null)
    const [activeStatus, setActiveStatus] = useState<ConversationStatus | null>(null)
    const [sidebarOpen, setSidebarOpen] = useState(false)

    const [messagesState, setMessagesState] =
        useState<BlockState<ChatMessageUI[]>>({
            isLoading: false,
            data: null
        })

    const [conversationsState, setConversationsState] =
        useState<BlockState<ConversationResponse[]>>({
            isLoading: true,
            data: null
        })

    const bottomRef = useRef<HTMLDivElement | null>(null)

    useEffect(() => {
        async function load() {
            try {
                setConversationsState({ isLoading: true, data: null })

                const data = await AssistantService.loadConversations()

                setConversationsState({
                    isLoading: false,
                    data
                })

            } catch (e) {
                console.error("Failed to load conversations", e)

                setConversationsState({
                    isLoading: false,
                    data: null
                })
            }
        }

        void load()
    }, [])

    useEffect(() => {
        async function load() {
            if (!conversationId) {
                setMessagesState({ isLoading: false, data: null })
                return
            }
            try {
                setMessagesState({ isLoading: true, data: null })

                const data =
                    await AssistantService.loadMessages(conversationId)

                const mapped: ChatMessageUI[] =
                    data.map((m: ChatMessageResponse) => ({
                        id: m.id,
                        content: m.content,
                        sender: m.sender
                    }))

                setMessagesState({
                    isLoading: false,
                    data: mapped
                })
            } catch (e) {
                console.error("Failed to load messages", e)

                setMessagesState({
                    isLoading: false,
                    data: null
                })
            }
        }

        void load()
    }, [conversationId])

    useEffect(() => {
        bottomRef.current?.scrollIntoView({ behavior: "smooth" })
    }, [messagesState.data, messagesState.isLoading])

    async function handleSend(text: string) {

        const userMessage: ChatMessageUI = {
            id: crypto.randomUUID(),
            content: text,
            sender: MessageSender.USER
        }

        setMessagesState(prev => ({
            isLoading: true,
            data: prev.data
                ? [...prev.data, userMessage]
                : [userMessage]
        }))

        try {
            const response =
                await AssistantService.sendMessage(
                    text,
                    conversationId ?? undefined
                )

            if (!conversationId) {
                const updated =
                    await AssistantService.loadConversations()

                setConversationsState({
                    isLoading: false,
                    data: updated
                })
            }

            setConversationId(response.conversationId)

            const assistantMessage: ChatMessageUI = {
                id: crypto.randomUUID(),
                content: response.message,
                sender: MessageSender.ASSISTANT
            }

            setMessagesState(prev => ({
                isLoading: false,
                data: prev.data
                    ? [...prev.data, assistantMessage]
                    : [assistantMessage]
            }))

            if (response.type === "CHAT_CLOSED") {
                setActiveStatus(ConversationStatus.CLOSED)
            }

        } catch (e) {
            console.error("Failed to send message", e)

            setMessagesState(prev => ({
                ...prev,
                isLoading: false
            }))
        }
    }

    async function handleDelete(id: string) {
        try {
            await AssistantService.deleteConversation(id)

            const updated =
                await AssistantService.loadConversations()

            setConversationsState({
                isLoading: false,
                data: updated
            })

            if (conversationId === id) {
                setConversationId(null)
                setMessagesState({
                    isLoading: false,
                    data: null
                })
            }

        } catch (e) {
            console.error("Failed to delete conversation", e)
        }
    }

    function handleSelectConversation(id: string) {
        const conv =
            conversationsState.data?.find(c => c.id === id)

        setActiveStatus(conv?.status ?? null)
        setConversationId(id)
    }

    function startNewChat() {
        setConversationId(null)
        setMessagesState({
            isLoading: false,
            data: null
        })
        setActiveStatus(null)
        setSidebarOpen(false)
    }

    const messages = messagesState.data ?? []
    const conversations = conversationsState.data ?? []

    return (
        <section className={styles.container}>
            <ChatSidebar
                open={sidebarOpen}
                conversations={conversations}
                activeId={conversationId}
                onSelect={handleSelectConversation}
                onNewChat={startNewChat}
                onClose={() => setSidebarOpen(false)}
                onDelete={handleDelete}
            />
            <div className={styles.topBar}>
                <button
                    onClick={() => setSidebarOpen(true)}
                    className={styles.menuBtn}
                >
                    <h3>AI Асистент</h3>
                </button>
            </div>

            <div className={styles.messages}>
                {messages.length === 0 &&
                    !messagesState.isLoading && (
                        <div className={styles.empty}>
                            Почніть діалог
                        </div>
                    )}

                {messages.map(m => (
                    <div
                        key={m.id}
                        className={`${styles.message}
                                ${m.sender === MessageSender.USER
                            ? styles.user
                            : styles.assistant}`}
                    >
                        {m.content}
                    </div>
                ))}

                {messagesState.isLoading && (
                    <div className={`${styles.message} ${styles.assistant}`}>
                        <div className={styles.typing}>
                            <span />
                            <span />
                            <span />
                        </div>
                    </div>
                )}

                <div ref={bottomRef} />
            </div>

            <MessageInput
                onSend={handleSend}
                loading={messagesState.isLoading}
                disabled={activeStatus === ConversationStatus.CLOSED}
            />
        </section>
    )
}