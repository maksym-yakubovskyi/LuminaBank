import type {LoanResponse} from "@/domain/loan/loan.types.ts";
import LoanService from "@/application/account/loan.service.ts";
import {useEffect, useState} from "react";
import type {Account} from "@/domain/account/account.types.ts";
import type {BlockState} from "@/domain/shared/block-state.type.ts";
import LoanList from "@/presentation/components/loan/LoanList.tsx";
import LoanDetails from "@/presentation/components/loan/LoanDetails.tsx";
import LoanCreateForm from "@/presentation/components/loan/LoanCreateForm.tsx";
import {Grid} from "@/presentation/ui/grid/Grid.tsx";

export default function LoanPage() {

    const [loansState, setLoansState] = useState<BlockState<LoanResponse[]>>({
        isLoading: true,
        data: null
    })

    const [accounts, setAccounts] = useState<Account[]>([])
    const [selectedLoan, setSelectedLoan] = useState<LoanResponse | null>(null)

    useEffect(() => {
        void loadData()
    }, [])

    async function loadData() {
        try {
            const loans = await LoanService.getMyLoans()
            const accounts = await LoanService.getAvailableCreditAccounts()

            setLoansState({ isLoading: false, data: loans })
            setAccounts(accounts)

        } catch (e) {
            console.error("Loan page load failed", e)
            setLoansState({ isLoading: false, data: [] })
            setAccounts([])
        }
    }

    const selectedAccount = selectedLoan
        ? accounts.find(acc => acc.id === selectedLoan.creditAccountId)
        : null

    return (
        <Grid
            left={
                <LoanList
                    loans={loansState.data}
                    loading={loansState.isLoading}
                    onSelect={setSelectedLoan}
                />
            }
            right={
                <>
                    {selectedLoan ? (
                        <LoanDetails loan={selectedLoan} currency={selectedAccount?.currency}/>
                    ) : (
                        <LoanCreateForm
                            accounts={accounts}
                            onCreated={async () => {
                                setSelectedLoan(null)
                                await loadData()
                            }}
                        />
                    )}
                </>
            }
        />
    )
}