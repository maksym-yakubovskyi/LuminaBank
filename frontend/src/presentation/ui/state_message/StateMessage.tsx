import styles from "./StateMessage.module.css"
import type { ReactNode } from "react"

interface Props {
    children: ReactNode
}

export function StateMessage({ children }: Props) {
    return (
        <div className={styles.state}>
            {children}
        </div>
    )
}