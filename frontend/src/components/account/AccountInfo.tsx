import type {Card} from "@/features/types/card.ts";
import type {Account} from "@/features/types/account.ts";
import {InfoRow} from "@/components/info_row/InfoRow.tsx";

interface Props {
    card: Card | null
    account: Account | null
}

export default function AccountInfo({card , account}: Props) {
    if (!card || !account) {
        return <>Виберіть рахунок...</>
    }

    return (
        <section
            style={{
                border: "1px solid #ddd",
                padding: "16px",
                display: "grid",
                gap: "16px",
            }}
        >
            <div
                style={{
                    border: "1px solid #eee",
                    padding: "12px",
                    borderRadius: "6px",
                }}
            >
                <h4>Картка</h4>

                <InfoRow label="Тип картки" value={card.cardType} />
                <InfoRow label="Платіжна система" value={card.cardNetwork} />
                <InfoRow label="Номер картки" value={card.cardNumber} />
                <InfoRow label="Термін дії" value={card.expirationDate} />
                <InfoRow label="Тип рахунку" value={card.accountType} />
                <InfoRow label="Статус" value={card.status} />
                <InfoRow label="Ліміт" value={String(card.limit)} />
            </div>

            <div
                style={{
                    border: "1px solid #eee",
                    padding: "12px",
                    borderRadius: "6px",
                }}
            >
                <h4>Рахунок</h4>

                <InfoRow
                    label="Баланс"
                    value={`${account.balance} ${account.currency}`}
                />
                <InfoRow label="IBAN" value={account.iban} />
                <InfoRow label="Тип рахунку" value={account.type} />
                <InfoRow label="Статус" value={account.status} />
            </div>
        </section>
    )
}
