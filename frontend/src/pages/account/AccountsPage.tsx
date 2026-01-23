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

export default function AccountsPage() {
    const [cards, setCards] = useState<Card[]>([])
    const [account, setAccount] = useState<Account | null>(null)
    const { cardId } = useParams<{ cardId: string }>()
    const selectedId = cardId ? Number(cardId) : null
    const [isCreateMode, setIsCreateMode] = useState(false)

    useEffect(() => {
        async function loadData(){
            const accounts = await AccountService.getMyAccounts()
            const acc = accounts[0]
            if (!acc) return
            setAccount(acc)

            const cards = await CardService.getCardsByAccount(acc.id)
            setCards(cards)
        }

        loadData().catch(console.error)
    }, [])

    const selectedCard= cards?.find(tx => tx.id === selectedId) ?? null

    return (
        <>
            <section
                style={{
                    border: "1px solid #ddd",
                    padding: "16px",
                }}
            >
            <AccountList cards={cards}/>
            <Button
                onClick={() => setIsCreateMode(true)}
                style={{ marginTop: "16px", width: "100%" }}
            >Додати картку</Button>
            </section>

            <section
                style={{
                    border: "1px solid #ddd",
                    padding: "16px",
                }}
            >
                {isCreateMode ? (
                    <CreateCardForm
                        account={account}
                        onCancel={() => setIsCreateMode(false)}

                    />
                ) : (
                    <AccountInfo card={selectedCard} account={account} />
                )}
            </section>
        </>
    )
}