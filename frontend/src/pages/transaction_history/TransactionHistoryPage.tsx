import {useEffect, useState} from "react";
import AccountService from "@/api/service/AccountService.ts";
import TransactionHistoryService from "@/api/service/TransactionHistoryService.ts";
import type {TransactionHistoryItem} from "@/features/types/transactionHistoryItem.ts";
import {TransactionHistoryBlock} from "@/components/dashboard/TransactionHistoryBlock.tsx";
import TransactionBlock from "@/components/transaction_history/TransactionBlock.tsx";
import {useParams} from "react-router-dom";
import type {BlockState} from "@/features/state/state.ts";

export default function TransactionHistoryPage(){
    const { paymentId } = useParams<{ paymentId: string }>()
    const selectedId = paymentId ? Number(paymentId) : null

    const [historyState, setHistoryState] = useState<BlockState<TransactionHistoryItem[]>>({
        isLoading: true,
        data: null,
    })

    useEffect(() => {
        void loadData()
    }, [])

    async function loadData(){
        try{
            const accounts  = await AccountService.getMyAccounts()

            if (accounts.length === 0) {
                setHistoryState({ isLoading: false, data: [] })
                return
            }

            const histories = await Promise.all(
                accounts.map(acc =>
                    TransactionHistoryService.getAllTransactionHistory(acc.id)
                )
            )

            const allHistory = histories
                .flat()
                .sort(
                    (a, b) =>
                        new Date(b.date).getTime() -
                        new Date(a.date).getTime()
                )

            setHistoryState({
                isLoading: false,
                data: allHistory,
            })

        }catch (e) {
            console.error("Transaction history load failed", e)
            setHistoryState({ isLoading: true, data: null })
        }
    }

    const selectedTransaction =
        historyState.data?.find(tx => tx.paymentId === selectedId) ?? null

    return(
        <>
            <section
                style={{
                    border: "1px solid #ddd",
                    padding: "16px",
                }}
            >
                <TransactionHistoryBlock
                    history={historyState.data}
                    loading={historyState.isLoading}
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