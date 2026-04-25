import {Grid} from "@/presentation/ui/grid/Grid.tsx";
import type {BusinessProfile, UserProfile} from "@/domain/user/user-profile.types.ts";
import AdminUserService from "@/application/admin/AdminUserService.ts";
import {useState} from "react";
import {UserSearch} from "@/presentation/components/operator/UserSearch.tsx";
import type {BlockState} from "@/domain/shared/block-state.type.ts";
import {IndividualUserInfoBlock} from "@/presentation/components/operator/IndividualUserInfoBlock.tsx";
import {BusinessUserInfoBlock} from "@/presentation/components/operator/BusinessUserInfoBlock.tsx";
import type {Account} from "@/domain/account/account.types.ts";
import AccountAdminService from "@/application/admin/AdminAccountService.ts";
import {AccountList} from "@/presentation/components/operator/AccountsList.tsx";
import type {TransactionHistoryItem} from "@/domain/transaction/transaction.types.ts";
import type {PaymentStatus} from "@/domain/payment/payment-status-enum.ts";
import AdminTransactionHistoryService from "@/application/admin/AdminTransactionHistoryService.ts";
import {TransactionsBlock} from "@/presentation/components/operator/TransactionsBlock.tsx";
import { Button } from "@/presentation/ui/button/Button";

type OperatorUserState =
    | { type: "INDIVIDUAL_USER"; data: UserProfile }
    | { type: "BUSINESS_USER"; data: BusinessProfile }

export default function OperatorPage() {

    const [state, setState] = useState<BlockState<OperatorUserState>>({
        isLoading: false,
        data: null
    })

    const [accountsState, setAccountsState] = useState<BlockState<Account[]>>({
        isLoading: false,
        data: null
    })

    const [transactionsState, setTransactionsState] = useState<BlockState<TransactionHistoryItem[]>>({
        isLoading: false,
        data: null
    })

    const [selectedAccountId, setSelectedAccountId] = useState<number | null>(null)
    const [selectedUserId, setSelectedUserId] = useState<number | null>(null)

    const [statusFilter, setStatusFilter] = useState<PaymentStatus | "ALL">("ALL")

    async function handleSearch(id: number) {
        try {
            setState({ isLoading: true, data: null })
            setAccountsState({ isLoading: true, data: null })
            setSelectedAccountId(null)
            setTransactionsState({ isLoading: false, data: null })
            setSelectedUserId(id)

            const user = await AdminUserService.getAnyUser(id)
            const accounts = await AccountAdminService.getAccountsByUserId(id)

            if ("companyName" in user) {
                setState({
                    isLoading: false,
                    data: {
                        type: "BUSINESS_USER",
                        data: user
                    }
                })
            } else {
                setState({
                    isLoading: false,
                    data: {
                        type: "INDIVIDUAL_USER",
                        data: user
                    }
                })
            }

            setAccountsState({
                isLoading: false,
                data: accounts
            })

        } catch (e) {
            console.error(e)
            setState({ isLoading: false, data: null })
            setAccountsState({ isLoading: false, data: null })
        }
    }

    async function loadTransactions(accountId: number, status?: PaymentStatus) {
        if (!selectedUserId) return
        try {
            setTransactionsState({ isLoading: true, data: null })

            const data = await AdminTransactionHistoryService.getTransactions(selectedUserId,accountId, status)

            setTransactionsState({
                isLoading: false,
                data
            })
            console.log(data)

        } catch (e) {
            console.error(e)
            setTransactionsState({ isLoading: false, data: null })
        }
    }

    function handleAccountClick(accountId: number) {
        setSelectedAccountId(accountId)
        setStatusFilter("ALL")
        loadTransactions(accountId)
    }

    function handleFilterChange(status: PaymentStatus | "ALL") {
        if (!selectedAccountId) return

        setStatusFilter(status)

        if (status === "ALL") {
            loadTransactions(selectedAccountId)
        } else {
            loadTransactions(selectedAccountId, status)
        }
    }

    const data = state.data

    return (
        <Grid
            left={
            <>
                <UserSearch
                    onSearch={handleSearch}
                    loading={state.isLoading}
                />

                {data?.type === "INDIVIDUAL_USER" && (
                    <IndividualUserInfoBlock
                        user={data.data}
                        loading={state.isLoading}
                    />
                )}

                {data?.type === "BUSINESS_USER" && (
                    <BusinessUserInfoBlock
                        business={data.data}
                        loading={state.isLoading}
                    />
                )}
            </>}
            right={
            <>
                {!selectedAccountId && (
                    <AccountList
                        accounts={accountsState.data}
                        loading={accountsState.isLoading}
                        onSelect={handleAccountClick}
                    />
                )}

                {selectedAccountId && (
                    <>
                        <Button
                            onClick={() => setSelectedAccountId(null)}
                        >
                            ← Назад до рахунків
                        </Button>

                        <TransactionsBlock
                            transactions={transactionsState.data}
                            loading={transactionsState.isLoading}
                            currentFilter={statusFilter}
                            onFilterChange={handleFilterChange}
                        />
                    </>
                )}
            </>
        }
        />
    )
}