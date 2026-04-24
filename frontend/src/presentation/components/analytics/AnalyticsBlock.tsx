import type {
    AnalyticsCategoryResponse,
    AnalyticsMonthlyOverviewResponse,
    AnalyticsTopRecipientResponse
} from "@/domain/analytics/analytics.types.ts";
import {CategoryPieChart} from "@/presentation/components/analytics/CategoryPieChart.tsx";
import type {Currency} from "@/domain/shared/currency.enum.ts";
import { StateMessage } from "@/presentation/ui/state_message/StateMessage"
import styles from "./AnalyticsBlock.module.css"
import {formatAmount} from "@/shared/utils/helpers.ts";

interface Props {
    monthOverview: AnalyticsMonthlyOverviewResponse | null
    topRecipients: AnalyticsTopRecipientResponse[] | null
    categories: AnalyticsCategoryResponse[] | null
    currency: Currency
    loading: boolean
}

export function AnalyticsBlock({monthOverview, topRecipients, categories,currency,loading,}: Props){
    if (loading)
        return <StateMessage>Завантаження...</StateMessage>

    return (
        <div className={styles.container}>

            {/* Категорії */}
            <section className={styles.section}>
                <h3 className={styles.sectionTitle}>
                    Витрати за категоріями
                </h3>

                {!categories || categories.length === 0 ? (
                    <div className={styles.stateText}>
                        Немає даних про витрати
                    </div>
                ) : (
                    <div className={styles.categoriesLayout}>

                        <div className={styles.chartWrapper}>
                            <CategoryPieChart data={categories} />
                        </div>

                        <div className={styles.categoryList}>
                            {categories.map(cat => (
                                <div
                                    key={cat.category}
                                    className={styles.categoryRow}
                                >
                                    <span className={styles.categoryName}>
                                        {cat.category}
                                    </span>

                                    <span className={styles.categoryValue}>
                                        {formatAmount(cat.totalAmount,currency)} ({cat.percentage}%)
                                    </span>
                                </div>
                            ))}
                        </div>

                    </div>
                )}
            </section>

            {/* Підсумок */}
            <section className={styles.section}>
                <h3 className={styles.sectionTitle}>
                    Підсумок за місяць
                </h3>

                {!monthOverview ? (
                    <div className={styles.stateText}>
                        Немає даних
                    </div>
                ) : (
                    <div className={styles.overviewGrid}>

                        <div className={styles.overviewItem}>
                            <div className={styles.overviewLabel}>
                                Надходження
                            </div>
                            <div className={`${styles.overviewValue} ${styles.income}`}>
                                {formatAmount(monthOverview.totalIncome,currency)}
                            </div>
                        </div>

                        <div className={styles.overviewItem}>
                            <div className={styles.overviewLabel}>
                                Витрати
                            </div>
                            <div className={`${styles.overviewValue} ${styles.expense}`}>
                                {formatAmount(monthOverview.totalExpense,currency)}
                            </div>
                        </div>

                        <div className={styles.overviewItem}>
                            <div className={styles.overviewLabel}>
                                Баланс
                            </div>
                            <div className={`${styles.overviewValue} ${
                                monthOverview.cashFlow >= 0
                                    ? styles.balancePositive
                                    : styles.balanceNegative
                            }`}>
                                {formatAmount(monthOverview.cashFlow,currency)}
                            </div>
                        </div>

                        <div className={styles.overviewItem}>
                            <div className={styles.overviewLabel}>
                                Кількість транзакцій
                            </div>
                            <div className={styles.overviewValue}>
                                {monthOverview.transactionCount}
                            </div>
                        </div>

                    </div>
                )}
            </section>

            {/* Топ отримувачі */}
            <section className={styles.section}>
                <h3 className={styles.sectionTitle}>
                    Топ отримувачів
                </h3>

                {!topRecipients || topRecipients.length === 0 ? (
                    <div className={styles.stateText}>
                        Ще немає отримувачів
                    </div>
                ) : (
                    <div className={styles.recipientList}>
                        {topRecipients.map(r => (
                            <div
                                key={r.recipientId}
                                className={styles.recipientRow}
                            >
                                <div>
                                    <div className={styles.recipientName}>
                                        {r.displayName}
                                    </div>
                                    <div className={styles.recipientMeta}>
                                        {r.transactionCount} платежів
                                    </div>
                                </div>

                                <div>
                                    {formatAmount(r.totalAmount,currency)}
                                </div>
                            </div>
                        ))}
                    </div>
                )}
            </section>

        </div>
    )
}