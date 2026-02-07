import {useEffect, useState} from "react";
import {Button} from "@/components/button/Button.tsx";

interface Props {
    expiresAt: number
    onCancel: () => void
    onClose: () => void
}

export function PaymentSuccessBanner({expiresAt, onClose,onCancel}: Props) {
    const [secondsLeft, setSecondsLeft] = useState(
        Math.max(0, Math.floor((expiresAt - Date.now()) / 1000))
    )

    useEffect(() => {
        const t = setInterval(() => {
            const left = Math.max(0, Math.floor((expiresAt - Date.now()) / 1000))
            setSecondsLeft(left)
            if (left === 0) clearInterval(t)
        }, 1000)

        return () => clearInterval(t)
    }, [expiresAt])

    return (
        <div
            style={{
                border: "1px solid #4caf50",
                background: "#e8f5e9",
                padding: "12px",
                borderRadius: "6px",
                marginBottom: "16px",
            }}
        >
            <b>✅ Оплата успішна</b>

            {secondsLeft > 0 ? (
                <div style={{ marginTop: "8px" }}>
                    <Button onClick={onCancel}>
                        Скасувати ({secondsLeft} с)
                    </Button>
                </div>
            ) : (
                <div style={{ marginTop: "8px", color: "#666" }}>
                    Час для скасування минув
                </div>
            )}

            <button
                onClick={onClose}
                style={{
                    float: "right",
                    border: "none",
                    background: "transparent",
                    cursor: "pointer",
                }}
            >
                ✕
            </button>
        </div>
    )
}