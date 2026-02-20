import type {Account} from "@/features/types/account.ts";
import {useState} from "react";
import LoanService from "@/api/service/LoanService.ts";
import {Button} from "@/components/button/Button.tsx";
import type {LoanApplicationRequest, LoanOfferResponse} from "@/features/types/loan.ts";
import type {BlockState} from "@/features/state/state.ts";

interface Props {
    accounts: Account[]
    onCreated: () => void
}

export default function LoanCreateForm({ accounts, onCreated}: Props) {

    const [amount, setAmount] = useState(0)
    const [selectedAccount, setSelectedAccount] = useState<number>()

    const [offers, setOffers] = useState<BlockState<LoanOfferResponse[]>>({
            isLoading: true,
            data: null,
    })

    const [approvingTerm, setApprovingTerm] = useState<number | null>(null)

    async function handleGetOffers() {
        try {
            if (!selectedAccount || amount <= 0) return

            const request: LoanApplicationRequest = {
                creditAccountId: selectedAccount,
                requestedAmount: amount,
                requestedTermMonths: 12 // значення неважливе для offers
            }

            const result = await LoanService.getLoanOffers(request)

            setOffers({
                isLoading: false,
                data: result
            })
        }catch (e) {
            console.error("Get offers failed", e)

            setOffers({
                isLoading: false,
                data: []
            })
        }
    }

    async function handleApprove(term: number) {

        if (!selectedAccount) return

        setApprovingTerm(term)

        try{
            const request: LoanApplicationRequest = {
                creditAccountId: selectedAccount,
                requestedAmount: amount,
                requestedTermMonths: term
            }

            await LoanService.approveLoan(request)

            setAmount(0)
            setSelectedAccount(undefined)
            setOffers({
                isLoading: false,
                data: null
            })

            onCreated()
        } catch (e) {
            console.error("Approve loan failed", e)
            alert("Не вдалося оформити кредит")
        } finally {
            setApprovingTerm(null)
        }
    }

    if (accounts.length === 0) {
        return <>Немає доступних кредитних рахунків</>
    }

    return (
        <div style={{ display: "grid", gap: 16 }}>

            <h3>Оформити кредит</h3>

            {/* ACCOUNT SELECT */}
            <div>
                <label>Кредитний рахунок</label>
                <select
                    value={selectedAccount}
                    onChange={e => setSelectedAccount(Number(e.target.value))}
                >
                    <option value="">Оберіть рахунок</option>
                    {accounts.map(acc => (
                        <option key={acc.id} value={acc.id}>
                            {acc.iban} ({acc.currency})
                        </option>
                    ))}
                </select>
            </div>

            {/* AMOUNT */}
            <div>
                <label>Сума</label>
                <input
                    type="number"
                    value={amount}
                    onChange={e => setAmount(Number(e.target.value))}
                />
            </div>

            {/* GET OFFERS */}
            <Button
                onClick={handleGetOffers}
                loading={offers.isLoading}
                disabled={!selectedAccount || amount <= 0}
            >
                Розрахувати пропозиції
            </Button>

            {/* OFFERS LIST */}
            {offers.data && offers.data.length > 0 && (
                <div style={{ display: "grid", gap: 12 }}>

                    <h4>Доступні пропозиції</h4>

                    {offers.data.map(offer => (
                        <div
                            key={offer.termMonths}
                            style={{
                                border: "1px solid #eee",
                                padding: 12
                            }}
                        >
                            <p>
                                <strong>{offer.termMonths} міс.</strong>
                            </p>
                            <p>Ставка: {offer.interestRate}%</p>
                            <p>Щомісячний платіж: {offer.monthlyPayment}</p>
                            <p>Загальна сума: {offer.totalPayable}</p>

                            <Button
                                onClick={() =>
                                    handleApprove(offer.termMonths)
                                }
                                loading={approvingTerm === offer.termMonths}
                            >
                                Підтвердити
                            </Button>
                        </div>
                    ))}
                </div>
            )}
            {offers.data &&
                offers.data.length === 0 &&
                !offers.isLoading && (
                    <>Немає доступних пропозицій</>
                )}
        </div>
    )
}
