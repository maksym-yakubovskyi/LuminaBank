import type {LoanResponse} from "@/features/types/loan.ts";
import LoanService from "@/api/service/LoanService.ts";
import {useEffect, useState} from "react";
import type {Account} from "@/features/types/account.ts";
import type {BlockState} from "@/features/state/state.ts";
import LoanList from "@/components/loan/LoanList.tsx";
import LoanDetails from "@/components/loan/LoanDetails.tsx";
import LoanCreateForm from "@/components/loan/LoanCreateForm.tsx";

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

    return (
        <>
            <section style={{ border: "1px solid #ddd", padding: 16 }}>
                <LoanList
                    loans={loansState.data}
                    loading={loansState.isLoading}
                    onSelect={setSelectedLoan}
                />
            </section>

            <section style={{ border: "1px solid #ddd", padding: 16 }}>
                {selectedLoan ? (
                    <LoanDetails loan={selectedLoan} />
                ) : (
                    <LoanCreateForm
                        accounts={accounts}
                        onCreated={async () => {
                            setSelectedLoan(null)
                            await loadData()
                        }}
                    />
                )}
            </section>
        </>
    )
}