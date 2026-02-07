import {CardInfoBlock} from "@/components/dashboard/CardInfoBlock.tsx";
import {useEffect, useState} from "react";
import type {Card} from "@/features/types/card.ts";
import AccountService from "@/api/service/AccountService.ts";
import CardService from "@/api/service/CardService.ts";
import type {TransactionHistoryItem} from "@/features/types/transactionHistoryItem.ts";
import TransactionHistoryService from "@/api/service/TransactionHistoryService.ts";
import {TransactionHistoryBlock} from "@/components/dashboard/TransactionHistoryBlock.tsx";
import type {Account} from "@/features/types/account.ts";
import type {BlockState} from "@/features/state/state.ts";

export default function DashboardPage() {
    const [accountState, setAccountState] = useState<BlockState<Account>>({
        isLoading: true,
        data: null,
    })
    const [cardState, setCardState] = useState<BlockState<Card>>({
        isLoading: true,
        data: null,
    })

    const [historyState, setHistoryState] = useState<BlockState<TransactionHistoryItem[]>>({
        isLoading: true,
        data: null,
    })

    useEffect(() => {
       void loadAccounts()
    }, [])

    async function loadAccounts() {
        try {
            const acc = (await AccountService.getMyAccounts())[0]

            setAccountState({ isLoading: false, data: acc })

            if (acc) {
                await loadCards(acc.id)
                await loadHistory(acc.id)
            } else {
                setCardState({ isLoading: false, data: null })
                setHistoryState({ isLoading: false, data: [] })
            }

        } catch (e) {
            console.error("Account load failed", e)

            setAccountState({ isLoading: true, data: null })
        }
    }

    async function loadCards(accountId: number) {
        try {
            const cards = await CardService.getCardsByAccount(accountId)
            setCardState({ isLoading: false, data: cards[0] ?? null })

        } catch (e) {
            console.error("Card load failed", e)
            setCardState({ isLoading: true, data: null })
        }
    }

    async function loadHistory(accountId: number) {
        try {
            const history = await TransactionHistoryService.getTransactionHistory(accountId)
            setHistoryState({ isLoading: false, data: history })

        } catch (e) {
            console.error("History load failed", e)
            setHistoryState({ isLoading: true, data: null })
        }
    }

    return (
        <>
            <section
                style={{
                    border: "1px solid #ddd",
                    padding: "16px",
                }}
            >
                <CardInfoBlock
                    card={cardState.data}
                    account={accountState.data}
                    loading={accountState.isLoading || cardState.isLoading} />

                <TransactionHistoryBlock history={historyState.data} loading={historyState.isLoading} />
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