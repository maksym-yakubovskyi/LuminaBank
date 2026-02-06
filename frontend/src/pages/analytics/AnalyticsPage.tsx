import {useEffect, useState} from "react";
import type {
    AnalyticsCategoryResponse,
    AnalyticsMonthlyOverviewResponse,
    AnalyticsTopRecipientResponse
} from "@/features/types/analytics.ts";
import AnalyticsService from "@/api/service/AnalyticsService.ts";
import AccountService from "@/api/service/AccountService.ts";
import {extractErrorMessage} from "@/api/apiError.ts";
import {AnalyticsBlock} from "@/components/analytics/AnalyticsBlock.tsx";
import {useNavigate} from "react-router-dom";
import {ReportBlock} from "@/components/analytics/ReportBlock.tsx";
import type {Account} from "@/features/types/account.ts";

export default function AnalyticsPage() {
    const [monthOverview, setMonthOverview] = useState<AnalyticsMonthlyOverviewResponse | null>(null)
    const [topRecipients, setTopRecipients] = useState<AnalyticsTopRecipientResponse[] | null>(null)
    const [categories, setCategories] = useState<AnalyticsCategoryResponse[] | null>(null)
    const [account,setAccount] = useState<Account | null>(null)
    const navigate = useNavigate()

    useEffect(() => {
        async function loadData(){
            try{
                const accounts = await AccountService.getMyAccounts()
                if (!accounts || accounts.length === 0) {
                    alert("Немає рахунків, будь ласка створіть для початку!")
                    navigate("/dashboard")
                    return
                }

                const acc = accounts[0]

                if(!acc){
                    alert("Немає рахунків, будь ласка створіть для початку!")
                    navigate("/dashboard")
                    return
                }
                setAccount(acc)

                const overview  = await AnalyticsService.getOverview(acc.id)
                setMonthOverview(overview)
            }catch (err:any){
                const  message= extractErrorMessage(err)
                alert("Помилка отримання" + message)
            }

            try {
                const recipients  = await AnalyticsService.getTopRecipients()
                setTopRecipients(recipients )
            }catch (err:any){
                const  message= extractErrorMessage(err)
                alert("Помилка отримання" + message)
            }

            try {
                const cats  = await AnalyticsService.getCategoriesAnalytics()
                setCategories(cats)
            }catch (err:any){
                const  message= extractErrorMessage(err)
                alert("Помилка отримання" + message)
            }
        }

        loadData().catch(console.error)
    },[])

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
                    monthOverview={monthOverview}
                    topRecipients={topRecipients}
                    categories={categories}
                    currency={account?.currency || "UAH"}
                />

            </section>

            <section
                style={{
                    border: "1px solid #ddd",
                    padding: "16px",
                }}
            >
                {!account && <p>Завантаження ...</p>}
                {account && <ReportBlock account={account} />}
            </section>
        </>
    )
}