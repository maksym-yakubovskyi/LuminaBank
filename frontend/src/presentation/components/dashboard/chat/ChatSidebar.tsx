import type { ConversationResponse } from "@/domain/assistant/assistant.types.ts"
import { formatDate } from "@/shared/utils/helpers.ts"
import styles from "./ChatSidebar.module.css"
import {ConversationStatus} from "@/domain/assistant/conversation-status.enum.ts";

interface Props {
    open: boolean
    conversations: ConversationResponse[]
    activeId: string | null
    onSelect: (id: string) => void
    onNewChat: () => void
    onClose: () => void
    onDelete: (id: string) => void
}

export function ChatSidebar({
                                open,
                                conversations,
                                activeId,
                                onSelect,
                                onNewChat,
                                onClose,
                                onDelete,
                            }: Props) {

    if (!open) return null

    return (
        <>
            <div
                className={styles.backdrop}
                onClick={onClose}
            />

            <aside className={styles.sidebar}>

                <div className={styles.header}>
                    <h3>Чати</h3>

                    <button
                        onClick={onNewChat}
                        className={styles.newBtn}
                    >
                        <h4>+ Новий</h4>
                    </button>
                </div>

                <div className={styles.list}>
                    {conversations.map(c => (
                        <div
                            key={c.id}
                            className={`${styles.item}
                            ${c.status === ConversationStatus.CLOSED ? styles.itemClosed : ""}
                        `}>
                            <div
                                onClick={() => {
                                    onSelect(c.id)
                                    onClose()
                                }}
                                className={`${styles.content}
                                    ${activeId === c.id ? styles.active : ""}`}
                            >
                                <div className={styles.date}>
                                    {formatDate(c.lastMessageAt)}
                                </div>

                                {c.status === ConversationStatus.CLOSED && (
                                    <span className={styles.closedBadge}>
                                        Закритий
                                    </span>
                                )}
                            </div>

                            <button
                                className={styles.deleteBtn}
                                onClick={(e) => {
                                    e.stopPropagation()
                                    onDelete(c.id)
                                }}
                            >
                                ✕
                            </button>
                        </div>
                    ))}
                </div>

            </aside>
        </>
    )
}