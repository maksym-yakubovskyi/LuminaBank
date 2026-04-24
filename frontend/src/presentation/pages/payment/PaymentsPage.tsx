import {useEffect, useState} from "react";
import {ToggleGroup} from "@/presentation/ui/button/ToggleGroup.tsx";
import {CardTransferForm, type CardTransferFormInputs} from "@/presentation/components/payment/CardTransferForm.tsx";
import {ServicePaymentForm, type ServicePaymentFormInputs} from "@/presentation/components/payment/ServicePaymentForm.tsx";
import type {Card} from "@/domain/card/card.types.ts";
import CardService from "@/application/account/card.service.ts";
import PaymentService from "@/application/payment/payment.service.ts";
import type {TemplateItem} from "@/domain/payment/template.types.ts";
import {PaymentTemplatesBlock} from "@/presentation/components/payment/PaymentTemplatesBlock.tsx";
import type {BlockState} from "@/domain/shared/block-state.type.ts";
import type {PaymentResponse} from "@/domain/payment/payment.types.ts";
import {PaymentSuccessBanner} from "@/presentation/components/payment/PaymentSuccessBanner.tsx";
import {PaymentTemplateType} from "@/domain/payment/payment-template-type.enum.ts";
import {RecurrenceType} from "@/domain/payment/recurrence-type.enum.ts";
import {Grid} from "@/presentation/ui/grid/Grid.tsx";
import styles from "./PaymentPage.module.css";

export default function PaymentsPage() {
    const [mode, setMode] = useState<"transfer" | "service">("transfer")
    const [paymentSuccess, setPaymentSuccess] = useState<PaymentResponse | null>(null);

    const [cardsState, setCardsState] = useState<BlockState<Card[]>>({
        isLoading: true,
        data: null,
    })

    const [templatesState, setTemplatesState] = useState<BlockState<TemplateItem[]>>({
        isLoading: true,
        data: null,
    })

    useEffect(() => {
        void loadData()
    }, [])

    async function loadData(){
        try{
            const cards = await CardService.getMyCards()
            setCardsState({ isLoading: false, data: cards })

            await reloadTemplates()
        }catch (e) {
            console.error("Payments page load failed", e)

            setCardsState({ isLoading: true, data: null })
            setTemplatesState({ isLoading: true, data: null })
        }
    }

    async function reloadTemplates() {
        try {
            const templates = await PaymentService.getMyPaymentTemplates()
            setTemplatesState({ isLoading: false, data: templates })
        } catch (e) {
            console.error("Templates reload failed", e)
            setTemplatesState({ isLoading: true, data: null })
        }
    }

    async function handleCardTransferSubmit(data: CardTransferFormInputs) {
        try {
            if (data.saveAsTemplate) {
                const recurrenceEnabled = data.recurrence.enabled

                await PaymentService.createPaymentTemplate({
                    name: data.templateName!,
                    fromCardNumber: data.fromCardNumber,
                    type: PaymentTemplateType.TRANSFER,
                    toCardNumber: data.toCardNumber,
                    amount: data.amount,
                    description: data.description ?? "",

                    recurrenceType: recurrenceEnabled ? data.recurrence.type! : RecurrenceType.NONE,
                    hour: recurrenceEnabled ? data.recurrence.hour : null,
                    minute: recurrenceEnabled ? data.recurrence.minute : null,
                    dayOfWeek: recurrenceEnabled ? data.recurrence.dayOfWeek : null,
                    dayOfMonth: recurrenceEnabled ? data.recurrence.dayOfMonth : null,
                })
                await reloadTemplates()
                setPaymentSuccess(null)
                return
            }

            const payment = await PaymentService.makePayment({
                fromCardNumber: data.fromCardNumber,
                toCardNumber: data.toCardNumber,
                amount: data.amount,
                description: data.description ?? "",
            })

            setPaymentSuccess(payment)
        }catch (e) {
            console.error("Card transfer failed", e)
            alert("Помилка відправки")
        }
    }

    async function handleServicePaymentSubmit(data: ServicePaymentFormInputs) {
        try {
            if (data.saveAsTemplate) {
                const recurrenceEnabled = data.recurrence.enabled

                await PaymentService.createPaymentTemplate({
                    name: data.templateName!,
                    fromCardNumber: data.fromCardNumber,
                    type: PaymentTemplateType.SERVICE,
                    providerId: data.providerId,
                    payerReference: data.payerReference,
                    category: data.category,
                    amount: data.amount,
                    description: data.description ?? "",

                    recurrenceType: recurrenceEnabled ? data.recurrence.type! : RecurrenceType.NONE,
                    hour: recurrenceEnabled ? data.recurrence.hour : null,
                    minute: recurrenceEnabled ? data.recurrence.minute : null,
                    dayOfWeek: recurrenceEnabled ? data.recurrence.dayOfWeek : null,
                    dayOfMonth: recurrenceEnabled ? data.recurrence.dayOfMonth : null,
                })
                await reloadTemplates()
                setPaymentSuccess(null)
                return
            }

            const payment = await PaymentService.makePaymentService({
                fromCardNumber: data.fromCardNumber,
                providerId: data.providerId,
                payerReference: data.payerReference,
                amount: data.amount,
                category: data.category,
                description: data.description ?? "",
            })
            setPaymentSuccess(payment)

        } catch (e) {
            console.error("Service payment failed", e)
            alert("Помилка відправки")
        }
    }

    function handleTemplateDeleted(id: number) {
        setTemplatesState(prev =>
            prev.data
                ? { isLoading: false, data: prev.data.filter(t => t.id !== id) }
                : prev
        )
    }

    return (
        <Grid
            left={
                <section className={styles.container}>
                    <h2 className={styles.title}>Оплати та перекази</h2>

                    <ToggleGroup
                        value={mode}
                        onChange={setMode}
                        options={[
                            { value: "transfer", label: "Переказ по карті" },
                            { value: "service", label: "Оплата послуг" },
                        ]}
                    />

                    <div className={styles.formWrapper}>
                        {mode === "transfer" ? (
                            <CardTransferForm
                                cards={cardsState.data ?? []}
                                loading={cardsState.isLoading}
                                onSubmit={handleCardTransferSubmit}
                            />
                        ) : (
                            <ServicePaymentForm
                                cards={cardsState.data ?? []}
                                loading={cardsState.isLoading}
                                onSubmit={handleServicePaymentSubmit}
                            />
                        )}
                    </div>

                    {paymentSuccess && (
                        <PaymentSuccessBanner
                            expiresAt={Date.now() + 30_000}
                            onClose={() => setPaymentSuccess(null)}
                            onCancel={async () => {
                                await PaymentService.cancelPayment(paymentSuccess.id)
                                setPaymentSuccess(null)
                            }}
                        />
                    )}
                </section>
            }
            right={
                <PaymentTemplatesBlock
                    templates={templatesState.data}
                    loading={templatesState.isLoading}
                    onDeleted={handleTemplateDeleted}
                />
            }
        />
    )
}