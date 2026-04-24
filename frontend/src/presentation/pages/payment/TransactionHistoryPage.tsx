import {useEffect, useState} from "react";
import AccountService from "@/application/account/account.service.ts";
import TransactionHistoryService from "@/application/payment/transaction-history.service.ts";
import type {TransactionHistoryItem} from "@/domain/transaction/transaction.types.ts";
import {TransactionHistoryBlock} from "@/presentation/components/dashboard/TransactionHistoryBlock.tsx";
import TransactionBlock from "@/presentation/components/transaction_history/TransactionBlock.tsx";
import {useParams} from "react-router-dom";
import type {BlockState} from "@/domain/shared/block-state.type.ts";
import {Grid} from "@/presentation/ui/grid/Grid.tsx";
import type {Account} from "@/domain/account/account.types.ts";
import { Select } from "@/presentation/ui/select/Select";

export default function TransactionHistoryPage(){
    const { paymentId } = useParams<{ paymentId: string }>()
    const selectedId = paymentId ? Number(paymentId) : null

    const [accounts, setAccounts] = useState<Account[]>([])
    const [selectedAccountId, setSelectedAccountId] = useState<number | null>(null)

    const [historyState, setHistoryState] = useState<BlockState<TransactionHistoryItem[]>>({
        isLoading: true,
        data: null,
    })

    useEffect(() => {
        void loadAccounts()
    }, [])

    async function loadAccounts() {
        try {
            const accs = await AccountService.getMyAccounts()

            setAccounts(accs)

            if (accs.length === 0) {
                setHistoryState({
                    isLoading: false,
                    data: [],
                })
                return
            }

            const firstAccountId = accs[0].id
            setSelectedAccountId(firstAccountId)

            await loadHistory(firstAccountId)

        } catch (e) {
            console.error("Accounts load failed", e)
            setHistoryState({
                isLoading: true,
                data: [],
            })
        }
    }

    async function loadHistory(accountId: number) {
        try {
            setHistoryState({
                isLoading: true,
                data: null,
            })

            const history =
                await TransactionHistoryService
                    .getAllTransactionHistory(accountId)

            setHistoryState({
                isLoading: false,
                data: history,
            })

        } catch (e) {
            console.error("Transaction history load failed", e)

            setHistoryState({
                isLoading: true,
                data: [],
            })
        }
    }

    async function handleAccountChange(accountId: number) {
        setSelectedAccountId(accountId)
        await loadHistory(accountId)
    }

    const selectedTransaction =
        historyState.data?.find(tx => tx.paymentId === selectedId) ?? null

    return(
        <Grid
            left={
            <>
                {accounts.length > 0 && (
                    <Select
                        label="Рахунок"
                        value={selectedAccountId ?? ""}
                        onChange={(e) =>
                            handleAccountChange(
                                Number(e.target.value)
                            )
                        }
                    >
                        {accounts.map(acc => (
                            <option
                                key={acc.id}
                                value={acc.id}
                            >
                                {acc.type} • {acc.currency} • {acc.iban.slice(-4)}
                            </option>
                        ))}
                    </Select>
                )}
                <TransactionHistoryBlock
                    history={historyState.data}
                    loading={historyState.isLoading}
                />
            </>
            }
            right={
                <TransactionBlock transaction={selectedTransaction}/>
            }
        />
    )
}