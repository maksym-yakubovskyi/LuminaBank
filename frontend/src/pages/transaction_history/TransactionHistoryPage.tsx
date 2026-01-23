import {useEffect, useState} from "react";
import AccountService from "@/api/service/AccountService.ts";
import TransactionHistoryService from "@/api/service/TransactionHistoryService.ts";
import type {TransactionHistoryItem} from "@/features/types/transactionHistoryItem.ts";
import {TransactionHistoryBlock} from "@/components/dashboard/TransactionHistoryBlock.tsx";
import TransactionBlock from "@/components/transaction_history/TransactionBlock.tsx";
import {useParams} from "react-router-dom";

export default function TransactionHistoryPage(){
    const { paymentId } = useParams<{ paymentId: string }>()

    const [history, setHistory] =
        useState<TransactionHistoryItem[] | null>(null)

    const selectedId = paymentId ? Number(paymentId) : null

    useEffect(() => {
        async function loadData(){
            const accounts = await AccountService.getMyAccounts()
            const acc = accounts[0]
            if (!acc) return

            const history =
                await TransactionHistoryService.getAllTransactionHistory(acc.id)
            setHistory(history)
        }

        loadData().catch(console.error)
    }, [])

    const selectedTransaction =
        history?.find(tx => tx.paymentId === selectedId) ?? null

    return(
        <>
            <section
                style={{
                    border: "1px solid #ddd",
                    padding: "16px",
                }}
            >
                <TransactionHistoryBlock
                    history={history}
                />
            </section>

            <section
                style={{
                    border: "1px solid #ddd",
                    padding: "16px",
                }}
            >
                <TransactionBlock transaction={selectedTransaction}/>
            </section>
        </>
    )
}