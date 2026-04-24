import type {ReportResponse} from "@/domain/analytics/analytics.types.ts";
import {useEffect, useMemo, useRef, useState} from "react";
import AnalyticsService from "@/application/analytics/analytics.service.ts";
import {Button} from "@/presentation/ui/button/Button.tsx";
import type {Account} from "@/domain/account/account.types.ts";
import {ReportStatus} from "@/domain/analytics/report-status.enum.ts";
import styles from "./ReportBlock.module.css"
import {formatDate} from "@/shared/utils/helpers.ts";
import {StateMessage} from "@/presentation/ui/state_message/StateMessage.tsx";

interface Props {
    account: Account
}
export function ReportBlock({account}: Props) {
    const [reports, setReports] = useState<ReportResponse[]>([])
    const [initialLoading, setInitialLoading] = useState(true)
    const [creatingLoading, setCreatingLoading] = useState(false)

    const [month, setMonth] = useState("2026-02")
    const [from, setFrom] = useState("2026-02-01")
    const [to, setTo] = useState("2026-02-27")

    const pollingRefs = useRef<Map<string, number>>(new Map())

    const { isMonthlyValid, isDateRangeValid } = useMemo(() => {
        const isMonthlyValid =
            Boolean(month) && /^\d{4}-\d{2}$/.test(month)

        const isDateRangeValid =
            Boolean(from) &&
            Boolean(to) &&
            from <= to

        return {
            isMonthlyValid,
            isDateRangeValid,
        }
    }, [month, from, to])

    useEffect(() => {
        void loadReports()
        return stopAllPolling
    }, [])

    async function loadReports() {
        try {
            setInitialLoading(true)
            const list = await AnalyticsService.getMyReports()
            setReports(list)
            list.forEach(startPollingIfNeeded)
        } catch (e) {
            console.error("Reports load failed", e)
        } finally {
            setInitialLoading(false)
        }
    }

    async function createReport(action: () => Promise<ReportResponse>) {
        try {
            setCreatingLoading(true)
            const created = await action()
            setReports(prev => [created, ...prev])
            startPollingIfNeeded(created)
        } catch (e) {
            console.error("Create report failed", e)
            alert("Помилка створення")
        } finally {
            setCreatingLoading(false)
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
        <div className={styles.container}>

            <h2 className={styles.title}>Звіти</h2>

            {/* CREATE SECTION */}
            <div className={styles.createSection}>

                {/* Місячний */}
                <div className={styles.row}>
                    <input
                        type="month"
                        value={month}
                        onChange={e => setMonth(e.target.value)}
                        className={styles.input}
                    />

                    <Button
                        loading={creatingLoading}
                        disabled={!isMonthlyValid}
                        onClick={() =>
                            createReport(() =>
                                AnalyticsService.createMonthlyReport(account.id, month)
                            )
                        }
                    >
                        + Місячний фінансовий
                    </Button>
                </div>

                {/* Денний */}
                <div className={styles.row}>
                    <input
                        type="date"
                        value={from}
                        onChange={e => setFrom(e.target.value)}
                        className={styles.input}
                    />

                    <input
                        type="date"
                        value={to}
                        onChange={e => setTo(e.target.value)}
                        className={styles.input}
                    />

                    <Button
                        loading={creatingLoading}
                        disabled={!isDateRangeValid}
                        onClick={() =>
                            createReport(() =>
                                AnalyticsService.createDailyReport(from, to)
                            )
                        }
                    >
                        + Денна активність
                    </Button>

                    <Button
                        loading={creatingLoading}
                        disabled={!isDateRangeValid}
                        onClick={() =>
                            createReport(() =>
                                AnalyticsService.createTransactionHistoryReport(from, to)
                            )
                        }
                    >
                        + Історія транзакцій
                    </Button>
                </div>

                {!isDateRangeValid && (
                    <div className={styles.validationText}>
                        Дата FROM не може бути пізніше TO
                    </div>
                )}

            </div>

            {/* TABLE SECTION */}
            <div className={styles.tableSection}>
                {initialLoading ? (
                    <StateMessage>Завантаження звітів...</StateMessage>
                ) : reports.length === 0 ? (
                    <StateMessage>У вас поки що немає створених звітів</StateMessage>
                ) : (
                <table className={styles.table}>
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
                        <tr key={r.id} className={styles.tableRow}>

                            <td>{r.reportType}</td>

                            <td>
                                {formatDate(r.createdAt)}
                            </td>

                            <td>
                                    <span className={`
                                        ${styles.status}
                                        ${
                                        r.status === ReportStatus.PENDING
                                            ? styles.pending
                                            : r.status === ReportStatus.PROCESSING
                                                ? styles.processing
                                                : r.status === ReportStatus.READY
                                                    ? styles.ready
                                                    : styles.failed
                                    }
                                    `}>
                                        {r.status}
                                    </span>
                            </td>

                            <td>
                                {r.status === ReportStatus.READY && (
                                    <Button
                                        className={styles.actionButton}
                                        onClick={() =>
                                            AnalyticsService.downloadReport(r.id)
                                        }
                                    >
                                        ⬇ Завантажити
                                    </Button>
                                )}

                                {r.status === ReportStatus.FAILED && "❌"}
                            </td>

                        </tr>
                    ))}
                    </tbody>
                </table>
                )}
            </div>

        </div>
    )
}