import type {LoanResponse} from "@/features/types/loan.ts";
import {Button} from "@/components/button/Button.tsx";


interface Props {
    loans: LoanResponse[] | null
    loading: boolean
    onSelect: (loan: LoanResponse) => void
}

export default function LoanList({ loans, loading, onSelect }: Props) {

    if (loading) return <>Завантаження...</>
    if (!loans || loans.length === 0)
        return <>Активних кредитів немає</>

    return (
        <>
            <h3>Мої кредити</h3>

            <ul style={{ listStyle: "none", padding: 0 }}>
                {loans.map(loan => (
                    <li key={loan.id}
                        style={{
                            borderBottom: "1px solid #eee",
                            padding: "12px 0"
                        }}
                    >
                        <div style={{
                            display: "flex",
                            justifyContent: "space-between",
                            alignItems: "center"
                        }}>
                            <div>
                                <strong>{loan.principalAmount}</strong>
                                <div style={{ fontSize: 12 }}>
                                    Залишок: {loan.remainingPrincipal}
                                </div>
                            </div>

                            <Button onClick={() => onSelect(loan)}>
                                Деталі
                            </Button>
                        </div>
                    </li>
                ))}
            </ul>
        </>
    )
}
