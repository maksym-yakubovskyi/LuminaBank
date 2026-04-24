import {CardInfoBlock} from "@/presentation/components/dashboard/CardInfoBlock.tsx";
import {useEffect, useState} from "react";
import type {Card} from "@/domain/card/card.types.ts";
import AccountService from "@/application/account/account.service.ts";
import CardService from "@/application/account/card.service.ts";
import type {TransactionHistoryItem} from "@/domain/transaction/transaction.types.ts";
import TransactionHistoryService from "@/application/payment/transaction-history.service.ts";
import {TransactionHistoryBlock} from "@/presentation/components/dashboard/TransactionHistoryBlock.tsx";
import type {Account} from "@/domain/account/account.types.ts";
import type {BlockState} from "@/domain/shared/block-state.type.ts";
import {Grid} from "@/presentation/ui/grid/Grid.tsx";
import {ChatBlock} from "@/presentation/components/dashboard/chat/ChatBlock.tsx";

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
        <Grid
            left={
                <>
                    <CardInfoBlock
                        card={cardState.data}
                        account={accountState.data}
                        loading={
                            accountState.isLoading ||
                            cardState.isLoading
                        }
                    />

                    <TransactionHistoryBlock
                        history={historyState.data}
                        loading={historyState.isLoading}
                    />
                </>
            }
            right={
                <ChatBlock />
            }
        />
    )
}