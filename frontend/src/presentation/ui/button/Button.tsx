import type { ButtonHTMLAttributes, ReactNode } from "react";
import styles from "./Button.module.css";

interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
    children: ReactNode;
    loading?: boolean;
    variant?: "primary" | "danger";
}

export function Button({
                           loading = false,
                           children,
                           disabled,
                           className,
                           variant = "primary",
                           ...props
                       }: ButtonProps) {

    return (
        <button
            className={`
                ${styles.button}
                ${styles[variant]}
                ${className ?? ""}
            `}
            disabled={disabled || loading}
            {...props}
        >
            {loading ? "Завантаження..." : children}
        </button>
    )
}