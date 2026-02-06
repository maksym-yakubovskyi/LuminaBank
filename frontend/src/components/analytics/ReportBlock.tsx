import type {ReportResponse} from "@/features/types/analytics.ts";
import {useEffect, useRef, useState} from "react";
import AnalyticsService from "@/api/service/AnalyticsService.ts";
import {extractErrorMessage} from "@/api/apiError.ts";
import {Button} from "@/components/button/Button.tsx";
import type {Account} from "@/features/types/account.ts";
import {ReportStatus} from "@/features/enum/enum.ts";

interface Props {
    account: Account
}
export function ReportBlock({account}: Props) {
    const [reports, setReports] = useState<ReportResponse[]>([])
    const [loading, setLoading] = useState(false)

    const [month, setMonth] = useState("2026-02")

    const [from, setFrom] = useState("2026-02-01")
    const [to, setTo] = useState("2026-02-27")

    const pollingRefs = useRef<Map<string, number>>(new Map())

    useEffect(() => {
        loadReports().catch(console.error);
        return () => stopAllPolling()
    }, [])

    async function loadReports() {
        try {
            const list = await AnalyticsService.getMyReports()
            setReports(list)
            list.forEach(startPollingIfNeeded)
        } catch (err: any) {
            const  message=extractErrorMessage(err)
            alert("Помилка отримання" + message)
        }
    }

    async function createMonthlyReport() {
        if (!month) {
            alert("Оберіть місяць")
            return
        }
        try {
            setLoading(true)

            const created = await AnalyticsService.createMonthlyReport(
                account.id,
                month
            )

            setReports(prev => [created, ...prev])
            startPollingIfNeeded(created)

        } catch (err: any) {
            const  message= extractErrorMessage(err)
            alert("Помилка створення" + message)
        } finally {
            setLoading(false)
        }
    }

    async function createDailyReport() {
        if (!from || !to) {
            alert("Оберіть діапазон дат")
            return
        }
        if (from > to) {
            alert("Дата FROM не може бути пізніше TO")
            return
        }
        try {
            setLoading(true)

            const created = await AnalyticsService.createDailyReport(
                from,
                to
            )

            setReports(prev => [created, ...prev])
            startPollingIfNeeded(created)

        } catch (err: any) {
            const  message= extractErrorMessage(err)
            alert("Помилка створення" + message)
        } finally {
            setLoading(false)
        }
    }

    async function createTransactionHistoryReport() {
        if (!from || !to) {
            alert("Оберіть діапазон дат")
            return
        }
        if (from > to) {
            alert("Дата FROM не може бути пізніше TO")
            return
        }
        try {
            setLoading(true)

            const created = await AnalyticsService.createTransactionHistoryReport(
                from,
                to
            )

            setReports(prev => [created, ...prev])
            startPollingIfNeeded(created)

        } catch (err: any) {
            const  message= extractErrorMessage(err)
            alert("Помилка створення" + message)
        } finally {
            setLoading(false)
        }
    }

    async function downloadReport (id: string) {
        try {
            await AnalyticsService.downloadReport(id)
        } catch (err: any) {
            const  message= extractErrorMessage(err)
            alert("Помилка створення" + message)
        }
    }


    function startPollingIfNeeded(report: ReportResponse) {
        if (report.status === ReportStatus.READY || pollingRefs.current.has(report.id)) return

        const timer = setInterval(async () => {
            try {
                const status = await AnalyticsService.getReportStatus(report.id)

                setReports(prev =>
                    prev.map(r =>
                        r.id === report.id ? { ...r, status } : r
                    )
                )

                if (status === ReportStatus.READY || status === ReportStatus.FAILED) {
                    clearInterval(timer)
                    pollingRefs.current.delete(report.id)
                }
            } catch {
                clearInterval(timer)
                pollingRefs.current.delete(report.id)
            }
        }, 3000)

        pollingRefs.current.set(report.id, timer)
    }

    function stopAllPolling() {
        pollingRefs.current.forEach(clearInterval)
        pollingRefs.current.clear()
    }

    return(
        <>
            <h2>Звіти</h2>

            <div style={{ display: "flex", gap: "8px", marginBottom: 12 }}>
                {/* Місячний */}
                <input
                    type="month"
                    value={month}
                    onChange={e => setMonth(e.target.value)}
                />

                <Button loading={loading} onClick={createMonthlyReport}>
                    ➕ Місячний фінансовий
                </Button>
            </div>

            <div style={{ display: "flex", gap: "8px", marginBottom: 12 }}>
                {/* Денний */}
                <input
                    type="date"
                    value={from}
                    onChange={e => setFrom(e.target.value)}
                />
                <input
                    type="date"
                    value={to}
                    onChange={e => setTo(e.target.value)}
                />

                <Button loading={loading} onClick={createDailyReport}>
                    ➕ Денна активність
                </Button>

                <Button loading={loading} onClick={createTransactionHistoryReport}>
                    ➕ Історія транзакцій
                </Button>
            </div>

            <table width="100%" border={1} cellPadding={8}>
                <thead>
                <tr>
                    <th>Тип</th>
                    <th>Дата</th>
                    <th>Статус</th>
                    <th></th>
                </tr>
                </thead>
                <tbody>
                {reports.map(r => (
                    <tr key={r.id}>
                        <td>{r.reportType}</td>
                        <td>{new Date(r.createdAt).toLocaleString()}</td>
                        <td>{r.status}</td>
                        <td>
                            {r.status === ReportStatus.READY && (
                                <Button loading={loading} onClick={() => downloadReport(r.id)}>
                                    ⬇ Завантажити
                                </Button>
                            )}
                            {r.status === ReportStatus.FAILED && "❌ Помилка"}
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>
        </>
    )
}