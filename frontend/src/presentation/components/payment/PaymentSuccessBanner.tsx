import {useEffect, useState} from "react";
import {Button} from "@/presentation/ui/button/Button.tsx";
import styles from "./PaymentSuccessBanner.module.css"

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
        <div className={styles.container}>

            <button
                onClick={onClose}
                className={styles.closeButton}
            >
                ✕
            </button>

            <div className={styles.title}>
                ✅ Оплата успішна
            </div>

            {secondsLeft > 0 ? (
                <div className={styles.cancelWrapper}>
                    <Button variant="danger" onClick={onCancel}>
                        Скасувати ({secondsLeft} с)
                    </Button>
                </div>
            ) : (
                <div className={styles.expired}>
                    Час для скасування минув
                </div>
            )}

        </div>
    )
}