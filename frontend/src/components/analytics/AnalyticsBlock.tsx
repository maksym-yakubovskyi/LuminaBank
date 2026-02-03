import type {
    AnalyticsCategoryResponse,
    AnalyticsMonthlyOverviewResponse,
    AnalyticsTopRecipientResponse
} from "@/features/types/analytics.ts";
import {CategoryPieChart} from "@/components/analytics/CategoryPieChart.tsx";

interface Props {
    monthOverview: AnalyticsMonthlyOverviewResponse | null
    topRecipients: AnalyticsTopRecipientResponse[] | null
    categories: AnalyticsCategoryResponse[] | null
    currency: string
}

export function AnalyticsBlock({monthOverview, topRecipients, categories,currency}: Props){

    function formatMoney(value: number): string {
        if(!value) value = 0;
        return value + " " + currency
    }

    return (
        <div style={{ display: "flex", flexDirection: "column", gap: "32px" }}>

            {/* Категорії */}
            <div>
                <h3>Витрати за категоріями</h3>

                {!categories || categories.length === 0 ? (
                    <p>Немає даних про витрати за категоріями</p>
                ) : (
                    <div style={{ display: "flex", gap: "32px" }}>

                        <CategoryPieChart data={categories} />

                        <div>
                            {categories.map(cat => (
                                <div
                                    key={cat.category}
                                    style={{
                                        display: "flex",
                                        justifyContent: "space-between",
                                        width: 260
                                    }}
                                >
                                    <span>{cat.category}</span>
                                    <span>
                                        {formatMoney(cat.totalAmount)} ({cat.percentage}%)
                                    </span>
                                </div>
                            ))}
                        </div>
                    </div>
                )}
            </div>

            {/* 2 Підсумок за місяць */}
            <div>
                <h3>За місяць</h3>

                {!monthOverview ? (
                    <p>Немає даних за місяць</p>
                ) : (
                    <pre style={{ fontFamily: "monospace" }}>
+ Надходження: {formatMoney(monthOverview.totalIncome)}
                        - Витрати:     {formatMoney(monthOverview.totalExpenses)}
                        = Баланс:      {formatMoney(monthOverview.cashFlow)}

                        Транзакцій: {monthOverview.transactionCount}
                    </pre>
                )}
            </div>

            {/* 3 Топ отримувачів */}
            <div>
                <h3>Топ отримувачів</h3>

                {!topRecipients || topRecipients.length === 0 ? (
                    <p>Ще немає отримувачів для відображення</p>
                ) : (
                    <ol>
                        {topRecipients.map(r => (
                            <li
                                key={r.recipientId}
                                style={{
                                    display: "flex",
                                    justifyContent: "space-between",
                                    width: 360
                                }}
                            >
                                <span>{r.displayName}</span>
                                <span>
                                    {formatMoney(r.totalAmount)}
                                    {" "}({r.transactionCount} платежі)
                                </span>
                            </li>
                        ))}
                    </ol>
                )}
            </div>
        </div>
    )
}