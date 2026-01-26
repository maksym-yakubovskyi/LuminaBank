import {useEffect, useState} from "react";
import {ToggleGroup} from "@/components/button/ToggleGroup.tsx";
import {CardTransferForm, type CardTransferFormInputs} from "@/components/payment/CardTransferForm.tsx";
import {ServicePaymentForm, type ServicePaymentFormInputs} from "@/components/payment/ServicePaymentForm.tsx";
import type {Card} from "@/features/types/card.ts";
import CardService from "@/api/service/CardService.ts";
import PaymentService from "@/api/service/PaymentService.ts";
import type {TemplateItem} from "@/features/types/template.ts";
import {PaymentTemplatesBlock} from "@/components/payment/PaymentTemplatesBlock.tsx";
import {PaymentTemplateType, RecurrenceType} from "@/features/enum/enum.ts";

export default function PaymentsPage() {
    const [mode, setMode] = useState<"transfer" | "service">("transfer")
    const [cards, setCards] = useState<Card[]>([])
    const [templates, setTemplates] = useState<TemplateItem[]>([])

    useEffect(() => {
        async function loadData(){
            const cards = await CardService.getMyCards()
            setCards(cards)

            const templates = await PaymentService.getMyPaymentTemplates()
            setTemplates(templates)
        }
        loadData().catch(console.error)
    }, [])

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
                return
            }

            await PaymentService.makePayment({
                fromCardNumber: data.fromCardNumber,
                toCardNumber: data.toCardNumber,
                amount: data.amount,
                description: data.description ?? "",
            })
        } catch (e: any) {
            console.error(e)
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
                    amount: data.amount,
                    description: data.description ?? "",

                    recurrenceType: recurrenceEnabled ? data.recurrence.type! : RecurrenceType.NONE,
                    hour: recurrenceEnabled ? data.recurrence.hour : null,
                    minute: recurrenceEnabled ? data.recurrence.minute : null,
                    dayOfWeek: recurrenceEnabled ? data.recurrence.dayOfWeek : null,
                    dayOfMonth: recurrenceEnabled ? data.recurrence.dayOfMonth : null,
                })
                return
            }

            await PaymentService.makePaymentService({
                fromCardNumber: data.fromCardNumber,
                providerId: data.providerId,
                payerReference: data.payerReference,
                amount: data.amount,
                description: data.description ?? "",
            })
        } catch (e: any) {
            console.error(e)
        }
    }

    function handleTemplateDeleted(id: number) {
        setTemplates((prev) => prev.filter((t) => t.id !== id))
    }

    return (
        <>
            <section
                style={{
                    border: "1px solid #ddd",
                    padding: "16px",
                }}
            >
                <h2 style={{ marginBottom: "12px" }}>Оплати та перекази</h2>

                <ToggleGroup
                    value={mode}
                    onChange={setMode}
                    options={[
                        { value: "transfer", label: "Переказ по карті" },
                        { value: "service", label: "Оплата послуг" },
                    ]}
                />

                <div style={{ marginTop: "16px" }}>
                    {mode === "transfer" ? (
                        <CardTransferForm cards={cards} onSubmit={handleCardTransferSubmit} />
                    ) : (
                        <ServicePaymentForm cards={cards} onSubmit={handleServicePaymentSubmit} />
                    )}
                </div>
            </section>

            <section
                style={{
                    border: "1px solid #ddd",
                    padding: "16px",
                }}
            >
            <PaymentTemplatesBlock templates={templates} onDeleted={handleTemplateDeleted} />
            </section>
        </>
    )
}