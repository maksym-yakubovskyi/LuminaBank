import type {Account} from "@/domain/account/account.types.ts";
import {useState} from "react";
import LoanService from "@/application/account/loan.service.ts";
import {Button} from "@/presentation/ui/button/Button.tsx";
import type {LoanApplicationRequest, LoanOfferResponse} from "@/domain/loan/loan.types.ts";
import type {BlockState} from "@/domain/shared/block-state.type.ts";
import {StateMessage} from "@/presentation/ui/state_message/StateMessage.tsx";
import styles from "./LoanCreateForm.module.css"
import {Input} from "@/presentation/ui/input/Input.tsx";
import { Select } from "@/presentation/ui/select/Select";
import { z } from "zod"
import { useForm } from "react-hook-form"
import { zodResolver } from "@hookform/resolvers/zod"

const loanSchema = z.object({
    creditAccountId: z
        .number("Оберіть кредитний рахунок",)
        .int()
        .positive("Оберіть кредитний рахунок"),

    requestedAmount: z
        .number("Сума обов'язкова",)
        .min(0.01, "Сума повинна бути більше 0"),
})

type LoanFormInputs = z.infer<typeof loanSchema>

interface Props {
    accounts: Account[]
    onCreated: () => void
}

export default function LoanCreateForm({ accounts, onCreated}: Props) {
    const [offers, setOffers] = useState<BlockState<LoanOfferResponse[]>>({
            isLoading: false,
            data: null,
    })

    const [approvingTerm, setApprovingTerm] = useState<number | null>(null)

    const {
        register,
        handleSubmit,
        watch,
        reset,
        formState: { errors },
    } = useForm<LoanFormInputs>({
        resolver: zodResolver(loanSchema),
        defaultValues: {
            creditAccountId: 0,
            requestedAmount: 0,
        },
    })

    const selectedAccount = watch("creditAccountId")
    const amount = watch("requestedAmount")

    async function handleGetOffers(data: LoanFormInputs) {
        try {
            setOffers({
                isLoading: true,
                data: null
            })

            const request: LoanApplicationRequest = {
                creditAccountId: data.creditAccountId,
                requestedAmount: data.requestedAmount,
                requestedTermMonths: 12, // для offers неважливо
            }

            const result = await LoanService.getLoanOffers(request)

            console.log(result)

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
        if (!selectedAccount || !amount) return

        setApprovingTerm(term)

        try{
            const request: LoanApplicationRequest = {
                creditAccountId: selectedAccount,
                requestedAmount: amount,
                requestedTermMonths: term
            }

            await LoanService.approveLoan(request)

            reset()
            setOffers({
                isLoading: false,
                data: null,
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
        return <StateMessage>Немає доступних кредитних рахунків</StateMessage>
    }

    return (
        <div className={styles.container}>

            <h3 className={styles.title}>Оформити кредит</h3>

            {/* FORM */}
            <form
                onSubmit={handleSubmit(handleGetOffers)}
                className={styles.formCard}
            >
                <Select
                    label="Кредитний рахунок"
                    {...register("creditAccountId", {
                        valueAsNumber: true,
                    })}
                    error={errors.creditAccountId?.message}
                >
                    <option value={0}>
                        Оберіть рахунок
                    </option>

                    {accounts.map(acc => (
                        <option
                            key={acc.id}
                            value={acc.id}
                        >
                            {acc.iban} ({acc.currency})
                        </option>
                    ))}
                </Select>

                <Input
                    label="Сума"
                    type="number"
                    step="0.01"
                    min="0.01"
                    {...register("requestedAmount", {
                        valueAsNumber: true,
                    })}
                    error={errors.requestedAmount?.message}
                />

                <div className={styles.actions}>
                    <Button
                        type="submit"
                        loading={offers.isLoading}
                        disabled={!selectedAccount || amount <= 0}
                    >
                        Розрахувати пропозиції
                    </Button>
                </div>
            </form>

            {/* OFFERS */}
            {offers.data && offers.data.length > 0 && (
                <div className={styles.offersSection}>

                    <div className={styles.offersTitle}>
                        Доступні пропозиції
                    </div>

                    {offers.data.map(offer => (
                        <div key={offer.termMonths} className={styles.offerCard}>

                            <div className={styles.offerHeader}>
                                <div className={styles.term}>
                                    {offer.termMonths} міс.
                                </div>
                                <div className={styles.rate}>
                                    {offer.interestRate}% річних
                                </div>
                            </div>

                            <div className={styles.offerBody}>

                                <div>
                                    <div className={styles.offerLabel}>
                                        Щомісячний платіж
                                    </div>
                                    <div className={styles.offerValue}>
                                        {offer.monthlyPayment}
                                    </div>
                                </div>

                                <div>
                                    <div className={styles.offerLabel}>
                                        Загальна сума
                                    </div>
                                    <div className={styles.offerValue}>
                                        {offer.totalPayable}
                                    </div>
                                </div>

                            </div>

                            <Button
                                className={styles.offerAction}
                                onClick={() => handleApprove(offer.termMonths)}
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
                    <StateMessage>Немає доступних пропозицій</StateMessage>
                )}

        </div>
    )
}
