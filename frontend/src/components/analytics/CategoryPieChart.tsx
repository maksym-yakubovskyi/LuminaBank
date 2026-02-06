import {
    PieChart,
    Pie,
    Tooltip,
    Legend,
    ResponsiveContainer
} from "recharts"

import type { AnalyticsCategoryResponse } from "@/features/types/analytics"

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
        <div style={{  width: 350, height: 350, minWidth: 350, minHeight: 350}}>
            <ResponsiveContainer>
                <PieChart>
                    <Pie
                        data={chartData}
                        dataKey="totalAmount"
                        nameKey="category"
                        cx="50%"
                        cy="50%"
                        outerRadius={100}
                        label={({ name, percent }) =>
                            percent != null
                                ? `${name} ${(percent * 100).toFixed(0)}%`
                                : name
                        }
                    />

                    <Tooltip
                        formatter={(value) =>
                            typeof value === "number"
                                ? value.toFixed(2)
                                : ""
                        }
                    />

                    <Legend />
                </PieChart>
            </ResponsiveContainer>
        </div>
    )
}
