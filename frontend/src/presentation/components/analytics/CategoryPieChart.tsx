import {
    PieChart,
    Pie,
    Tooltip,
    Legend,
    ResponsiveContainer
} from "recharts"

import type { AnalyticsCategoryResponse } from "@/domain/analytics/analytics.types.ts"

interface CategoryPieChartProps {
    data: AnalyticsCategoryResponse[]
}

const COLORS = [
    "#0088FE",
    "#00C49F",
    "#FFBB28",
    "#FF8042",
    "#AF19FF",
    "#FF4560"
]

export function CategoryPieChart({ data }: CategoryPieChartProps) {

    // 🔹 додаємо кольори прямо в data
    const chartData = data.map((item, index) => ({
        ...item,
        fill: COLORS[index % COLORS.length]
    }))

    return (
        <ResponsiveContainer width="100%" height="100%">
            <PieChart>
                <Pie
                    data={chartData}
                    dataKey="totalAmount"
                    nameKey="category"
                    cx="50%"
                    cy="50%"
                    innerRadius="45%"
                    outerRadius="70%"
                    paddingAngle={2}
                    labelLine={false}
                />
                <Tooltip/>
                <Legend />
            </PieChart>
        </ResponsiveContainer>
    )
}
