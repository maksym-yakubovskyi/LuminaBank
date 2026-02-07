import {useEffect, useState} from "react";
import type {
    AnalyticsCategoryResponse,
    AnalyticsMonthlyOverviewResponse,
    AnalyticsTopRecipientResponse
} from "@/features/types/analytics.ts";
import AnalyticsService from "@/api/service/AnalyticsService.ts";
import AccountService from "@/api/service/AccountService.ts";
import {AnalyticsBlock} from "@/components/analytics/AnalyticsBlock.tsx";
import {ReportBlock} from "@/components/analytics/ReportBlock.tsx";
import type {Account} from "@/features/types/account.ts";
import type {BlockState} from "@/features/state/state.ts";

export default function AnalyticsPage() {
    const [accounts, setAccounts] = useState<Account[]>([])
    const [selectedAccount, setSelectedAccount] = useState<Account | null>(null)

    const [overviewState, setOverviewState] =
        useState<BlockState<AnalyticsMonthlyOverviewResponse>>({
            isLoading: true,
            data: null,
        })
    const [topRecipientsState, setTopRecipientsState] =
        useState<BlockState<AnalyticsTopRecipientResponse[]>>({
            isLoading: true,
            data: null,
        })
    const [categoriesState, setCategoriesState] =
        useState<BlockState<AnalyticsCategoryResponse[]>>({
            isLoading: true,
            data: null,
        })

    useEffect(() => {
        void loadAccounts()
    },[])

    async function loadAccounts() {
        try {
            const accs = await AccountService.getMyAccounts()
            setAccounts(accs)

            const initial = accs[0] ?? null
            setSelectedAccount(initial)

            if (!initial) {
                resetAnalytics()
                return
            }

            await loadAnalytics(initial.id)
        } catch (e) {
            console.error("Accounts load failed", e)
            resetAnalytics()
        }
    }

    useEffect(() => {
        if (selectedAccount) {
            void loadAnalytics(selectedAccount.id)
        }
    }, [selectedAccount])

    async function loadAnalytics(accountId: number) {
        try {
            setOverviewState(s => ({ ...s, isLoading: true }))
            setTopRecipientsState(s => ({ ...s, isLoading: true }))
            setCategoriesState(s => ({ ...s, isLoading: true }))

            const [overview, recipients, categories] = await Promise.all([
                AnalyticsService.getOverview(accountId),
                AnalyticsService.getTopRecipients(),
                AnalyticsService.getCategoriesAnalytics(),
            ])

            setOverviewState({ isLoading: false, data: overview })
            setTopRecipientsState({ isLoading: false, data: recipients })
            setCategoriesState({ isLoading: false, data: categories })
        } catch (e) {
            console.error("Analytics load failed", e)
            resetAnalytics()
        }
    }

    function resetAnalytics() {
        setOverviewState({ isLoading: false, data: null })
        setTopRecipientsState({ isLoading: false, data: [] })
        setCategoriesState({ isLoading: false, data: [] })
    }

    return(
        <>
            <section
                style={{
                    border: "1px solid #ddd",
                    padding: "16px",
                }}
            >
                <h2>Аналітика</h2>

                <AnalyticsBlock
                    monthOverview={overviewState.data}
                    topRecipients={topRecipientsState.data}
                    categories={categoriesState.data}
                    loading={
                        overviewState.isLoading ||
                        topRecipientsState.isLoading ||
                        categoriesState.isLoading
                    }
                    currency={selectedAccount?.currency ?? "UAH"}
                />

                {accounts.length > 0 && (
                    <div>
                        <label>Рахунок</label>
                        <select
                            value={selectedAccount?.id ?? ""}
                            onChange={(e) => {
                                const id = Number(e.target.value)
                                const acc =
                                    accounts.find(a => a.id === id) ?? null
                                setSelectedAccount(acc)
                            }}
                        >
                            {accounts.map(acc => (
                                <option key={acc.id} value={acc.id}>
                                    {acc.iban} ({acc.currency})
                                </option>
                            ))}
                        </select>
                    </div>
                )}

            </section>

            <section
                style={{
                    border: "1px solid #ddd",
                    padding: "16px",
                }}
            >
                {!selectedAccount ?(
                    <p>Завантаження...</p>
                ):(
                    <ReportBlock account={selectedAccount}/>
                )}
            </section>
        </>
    )
}