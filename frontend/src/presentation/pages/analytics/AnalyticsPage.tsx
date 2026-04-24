import {useEffect, useState} from "react";
import type {
    AnalyticsCategoryResponse,
    AnalyticsMonthlyOverviewResponse,
    AnalyticsTopRecipientResponse
} from "@/domain/analytics/analytics.types.ts";
import AnalyticsService from "@/application/analytics/analytics.service.ts";
import AccountService from "@/application/account/account.service.ts";
import {AnalyticsBlock} from "@/presentation/components/analytics/AnalyticsBlock.tsx";
import {ReportBlock} from "@/presentation/components/analytics/ReportBlock.tsx";
import type {Account} from "@/domain/account/account.types.ts";
import type {BlockState} from "@/domain/shared/block-state.type.ts";
import {Grid} from "@/presentation/ui/grid/Grid.tsx";
import {Currency} from "@/domain/shared/currency.enum.ts";
import styles from "./AnalyticsPageLeft.module.css"
import {Select} from "@/presentation/ui/select/Select.tsx";
import {StateMessage} from "@/presentation/ui/state_message/StateMessage.tsx";

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
        <Grid
            left={
                <div className={styles.container}>

                    <h2 className={styles.title}>
                        Аналітика
                    </h2>

                    {accounts.length > 0 && (
                        <div className={styles.accountSelectorCard}>
                            <div className={styles.selectorTitle}>
                                Оберіть рахунок
                            </div>

                            <Select
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
                            </Select>
                        </div>
                    )}

                    <AnalyticsBlock
                        monthOverview={overviewState.data}
                        topRecipients={topRecipientsState.data}
                        categories={categoriesState.data}
                        loading={
                            overviewState.isLoading ||
                            topRecipientsState.isLoading ||
                            categoriesState.isLoading
                        }
                        currency={selectedAccount?.currency ?? Currency.UAH}
                    />

                </div>
            }
            right={
                accounts.length === 0 ? (
                    <StateMessage>
                        Щоб створити звіт, спочатку потрібно створити рахунок
                    </StateMessage>
                ) : selectedAccount && (
                    <ReportBlock account={selectedAccount} />
                )
            }
        />
    )
}