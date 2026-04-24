import styles from "./Grid.module.css"
import type { ReactNode } from "react"

interface Props {
    left: ReactNode
    right: ReactNode
}

export function Grid({ left, right }: Props) {
    return (
        <div className={styles.grid}>
            <section className={styles.left}>
                {left}
            </section>

            <section className={styles.right}>
                {right}
            </section>
        </div>
    )
}