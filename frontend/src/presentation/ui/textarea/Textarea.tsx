import { forwardRef, type TextareaHTMLAttributes } from "react"
import styles from "./Textarea.module.css"

interface Props extends TextareaHTMLAttributes<HTMLTextAreaElement> {
    label?: string
    error?: string
    fullWidth?: boolean
}

export const Textarea = forwardRef<HTMLTextAreaElement, Props>(
    ({ label, error, fullWidth, className, ...props }, ref) => {
        return (
            <div
                className={`${styles.wrapper} ${
                    fullWidth ? styles.fullWidth : ""
                }`}
            >
                {label && <label className={styles.label}>{label}</label>}

                <textarea
                    ref={ref}
                    className={`${styles.textarea} ${
                        error ? styles.errorBorder : ""
                    } ${className ?? ""}`}
                    {...props}
                />

                {error && <p className={styles.error}>{error}</p>}
            </div>
        )
    }
)

Textarea.displayName = "Textarea"