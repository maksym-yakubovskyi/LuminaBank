import {useEffect, useState} from "react";
import AccountService from "@/api/service/AccountService.ts";
import AccountList from "@/components/account/AccountList.tsx";
import type {Card} from "@/features/types/card.ts";
import CardService from "@/api/service/CardService.ts";
import {useParams} from "react-router-dom";
import AccountInfo from "@/components/account/AccountInfo.tsx";
import type {Account} from "@/features/types/account.ts";
import {Button} from "@/components/button/Button.tsx";
import {CreateCardForm} from "@/components/account/CreateCardForm.tsx";
import type {BlockState} from "@/features/state/state.ts";

export default function AccountsPage() {
    const { cardId } = useParams<{ cardId: string }>()
    const selectedId = cardId ? Number(cardId) : null

    const [accounts, setAccounts] = useState<Account[]>([])

    const [cardsState, setCardsState] = useState<BlockState<Card[]>>({
        isLoading: true,
        data: null,
    })

    const [isCreateMode, setIsCreateMode] = useState(false)

    useEffect(() => {
       void loadData()
    }, [])

    async function loadData(){
        try{
            const accounts = await AccountService.getMyAccounts()
            setAccounts(accounts)

            if (accounts.length === 0) {
                setCardsState({ isLoading: false, data: [] })
                return
            }

            const cardsArrays = await Promise.all(
                accounts.map(a => CardService.getCardsByAccount(a.id))
            )

            const allCards = cardsArrays.flat()
            setCardsState({ isLoading: false, data: allCards })

        }catch (e) {
            console.error("Accounts page load failed", e)

            setCardsState({ isLoading: true, data: null })
        }
    }

    const selectedCard= cardsState.data?.find(tx => tx.id === selectedId) ?? null
    const selectedAccount =
        selectedCard
            ? accounts.find(a => a.id === selectedCard.accountId) ?? null
            : null

    return (
        <>
            <section
                style={{
                    border: "1px solid #ddd",
                    padding: "16px",
                }}
            >
            <AccountList
                cards={cardsState.data}
                loading={cardsState.isLoading}
            />
                <Button
                    onClick={() => setIsCreateMode(prev => !prev)}
                    style={{ marginTop: "16px", width: "100%" }}
                >
                    {isCreateMode ? "Переглянути дані" : "Додати картку"}
                </Button>
            </section>

            <section
                style={{
                    border: "1px solid #ddd",
                    padding: "16px",
                }}
            >
                {isCreateMode ? (
                    <CreateCardForm
                        accounts={accounts}
                        onCreated={async () => {
                            setIsCreateMode(false)
                            await loadData()
                        }}
                        onCancel={() => setIsCreateMode(false)}
                    />
                ) : (
                    <AccountInfo card={selectedCard} account={selectedAccount} />
                )}
            </section>
        </>
    )
}