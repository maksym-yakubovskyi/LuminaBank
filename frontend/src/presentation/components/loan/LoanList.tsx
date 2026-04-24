import type {LoanResponse} from "@/domain/loan/loan.types.ts";
import {Button} from "@/presentation/ui/button/Button.tsx";
import styles from "./LoanList.module.css"
import { StateMessage } from "@/presentation/ui/state_message/StateMessage"
import { formatAmount } from "@/shared/utils/helpers"
import  {Currency} from "@/domain/shared/currency.enum.ts";

interface Props {
    loans: LoanResponse[] | null
    loading: boolean
    onSelect: (loan: LoanResponse) => void
}

export default function LoanList({ loans, loading, onSelect }: Props) {

    if (loading)
        return <StateMessage>Завантаження...</StateMessage>

    if (!loans || loans.length === 0)
        return <StateMessage>Активних кредитів немає</StateMessage>

    return (
        <div className={styles.container}>

            <h3 className={styles.title}>Мої кредити</h3>

            <div className={styles.list}>

                {loans.map(loan => {

                    const statusClass =
                        loan.status === "ACTIVE"
                            ? styles.active
                            : loan.status === "OVERDUE"
                                ? styles.overdue
                                : styles.closed

                    return (
                        <div key={loan.id} className={styles.card}>

                            <div className={styles.left}>

                                <div className={styles.amount}>
                                    {formatAmount(loan.principalAmount, Currency.UAH)}
                                </div>

                                <div className={styles.meta}>
                                    Залишок: {formatAmount(loan.remainingPrincipal, Currency.UAH)}
                                </div>

                                <div className={`${styles.status} ${statusClass}`}>
                                    {loan.status}
                                </div>

                            </div>

                            <Button
                                className={styles.button}
                                onClick={() => onSelect(loan)}
                            >
                                Деталі
                            </Button>

                        </div>
                    )
                })}

            </div>

        </div>
    )
}
