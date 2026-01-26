import {CardInfoBlock} from "@/components/dashboard/CardInfoBlock.tsx";
import {useEffect, useState} from "react";
import type {Card} from "@/features/types/card.ts";
import AccountService from "@/api/service/AccountService.ts";
import CardService from "@/api/service/CardService.ts";
import type {TransactionHistoryItem} from "@/features/types/transactionHistoryItem.ts";
import TransactionHistoryService from "@/api/service/TransactionHistoryService.ts";
import {TransactionHistoryBlock} from "@/components/dashboard/TransactionHistoryBlock.tsx";
import type {Account} from "@/features/types/account.ts";

export default function DashboardPage() {
    const [card, setCard] = useState<Card | null>(null)
    const [account, setAccount] = useState<Account | null>(null)
    const [history, setHistory] = useState<TransactionHistoryItem[] | null>(null)
    const [loading, setLoading] = useState(true)

    useEffect(() => {
        async function loadDashboard() {
            try {
                const accounts = await AccountService.getMyAccounts()
                const acc = accounts[0]

                if (!acc) {
                    setAccount(null)
                    setCard(null)
                    setHistory([])
                    return
                }

                setAccount(acc)

                const cards = await CardService.getCardsByAccount(acc.id)
                setCard(cards[0] ?? null)

                const history = await TransactionHistoryService.getTransactionHistory(acc.id)
                setHistory(history)
            } finally {
                setLoading(false)
            }
        }

        loadDashboard().catch(console.error);
    }, [])


    return (
        <>
            <section
                style={{
                    border: "1px solid #ddd",
                    padding: "16px",
                }}
            >
                <CardInfoBlock card={card} account={account} loading={loading} />
                <TransactionHistoryBlock history={history}/>
            </section>

            <section
                style={{
                    border: "1px solid #ddd",
                    padding: "16px",
                }}
            >
                Правий блок (основний контент)
            </section>
        </>
    )
}