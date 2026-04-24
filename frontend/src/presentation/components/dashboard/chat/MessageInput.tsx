import { useState, type KeyboardEvent } from "react"
import { Textarea } from "@/presentation/ui/textarea/Textarea.tsx"
import { Button } from "@/presentation/ui/button/Button.tsx"
import styles from "./MessageInput.module.css"

interface Props {
    onSend: (value: string) => Promise<void> | void
    loading?: boolean
    disabled?: boolean
}

export function MessageInput({ onSend, loading, disabled }: Props) {

    const [value, setValue] = useState("")
    const isDisabled = disabled || loading

    async function handleSend() {
        if (!value.trim() || loading) return

        const text = value.trim()

        setValue("")
        onSend(text)
    }

    function handleKeyDown(e: KeyboardEvent<HTMLTextAreaElement>) {
        if (isDisabled) return

        if (e.key === "Enter" && !e.shiftKey) {
            e.preventDefault()
            void handleSend()
        }
    }

    return (
        <div className={`${styles.container} ${isDisabled ? styles.disabled : ""}`}>

            <Textarea
                fullWidth
                value={value}
                onChange={e => setValue(e.target.value)}
                onKeyDown={handleKeyDown}
                placeholder={
                    disabled
                        ? "Цей чат закритий"
                        : "Напишіть повідомлення..."
                }
                className={styles.textarea}
                disabled={isDisabled}
            />

            <Button
                onClick={handleSend}
                disabled={isDisabled || !value.trim()}
                className={styles.sendButton}
            >
                {loading ? "…" : "➤"}
            </Button>

        </div>
    )
}