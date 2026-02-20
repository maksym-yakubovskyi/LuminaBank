import type {LoanResponse} from "@/features/types/loan.ts";

interface Props {
    loan: LoanResponse
}

export default function LoanDetails({ loan }: Props) {

    return (
        <div style={{ display: "grid", gap: 16 }}>
            <h3>Деталі кредиту</h3>

            <div>
                <p>Сума: {loan.principalAmount}</p>
                <p>Ставка: {loan.interestRate}%</p>
                <p>Щомісячний платіж: {loan.monthlyPayment}</p>
                <p>Залишок: {loan.remainingPrincipal}</p>
                <p>Статус: {loan.status}</p>
            </div>

            <h4>Графік платежів</h4>

            <ul style={{ listStyle: "none", padding: 0 }}>
                {loan.installments.map(inst => (
                    <li key={inst.id}
                        style={{
                            borderBottom: "1px solid #eee",
                            padding: 8
                        }}
                    >
                        #{inst.installmentNumber} —
                        {inst.dueDate} —
                        {inst.totalAmount} —
                        {inst.status}
                    </li>
                ))}
            </ul>
        </div>
    )
}
