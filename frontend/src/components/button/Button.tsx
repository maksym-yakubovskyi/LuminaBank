import type {ButtonHTMLAttributes, ReactNode} from "react";

interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
    children: ReactNode
    loading?: boolean
}
export function Button({
    loading = false,
    children,
    disabled,
    ...props
                       }: ButtonProps) {
    return (
        <button
        disabled={disabled || loading}
        {...props}
        >
            {loading ? "Завантаження..." : children}
        </button>
    )
}