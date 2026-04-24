import type {TemplateItem} from "@/domain/payment/template.types.ts";
import PaymentService from "@/application/payment/payment.service.ts";
import {PaymentTemplateType} from "@/domain/payment/payment-template-type.enum.ts";
import { StateMessage } from "@/presentation/ui/state_message/StateMessage"
import { Button } from "@/presentation/ui/button/Button"
import styles from "./PaymentTemplatesBlock.module.css"
import {formatDate} from "@/shared/utils/helpers.ts";

interface Props {
    templates: TemplateItem[] | null
    loading: boolean
    onDeleted: (id: number) => void
}

export function PaymentTemplatesBlock ({templates,loading, onDeleted}:Props){
    if (loading)
        return <StateMessage>Завантаження...</StateMessage>

    if (!templates || templates.length === 0)
        return <StateMessage>Список шаблонів порожній</StateMessage>


    function maskCard(cardNumber: string) {
        return `**** ${cardNumber.slice(-4)}`
    }

    async function deletePaymentTemplate(id: number) {
        try {
            await PaymentService.deletePaymentTemplate(id)
            onDeleted(id)
        } catch (e) {
            console.error("Delete template failed", e)
            alert("Помилка видалення")
        }
    }

    async function makeTemplatePayment(id: number){
        try{
            await PaymentService.makeTemplatePayment(id)
            alert("Оплата пройшла успішно")
        }catch (e) {
            console.error("Template execution failed", e)
            alert("Помилка відправки")
        }
    }

    return (
        <section className={styles.container}>

            <h3 className={styles.title}>Шаблони платежів</h3>

            <div className={styles.list}>
                {templates.map((t) => (

                    <div key={t.id} className={styles.card}>

                        {/* HEADER */}
                        <div className={styles.header}>

                            <div>
                                <div className={styles.name}>
                                    {t.name}
                                </div>

                                <div className={styles.type}>
                                    {t.type === PaymentTemplateType.TRANSFER
                                        ? "Переказ"
                                        : "Оплата послуги"}
                                </div>
                            </div>

                            <div style={{ textAlign: "right" }}>
                                <div className={styles.amount}>
                                    {t.amount}
                                </div>

                                <div
                                    className={
                                        t.isRecurring
                                            ? styles.recurring
                                            : styles.oneTime
                                    }
                                >
                                    {t.isRecurring
                                        ? "Регулярний"
                                        : "Разовий"}
                                </div>
                            </div>

                        </div>

                        <div className={styles.body}>

                            <div>
                                <span className={styles.label}>З карти:</span>{" "}
                                <b>{maskCard(t.fromCardNumber)}</b>
                            </div>

                            {t.type === PaymentTemplateType.TRANSFER && (
                                <div>
                                    <span className={styles.label}>На карту:</span>{" "}
                                    <b>{maskCard(t.toCardNumber)}</b>
                                </div>
                            )}

                            {t.description && (
                                <div>
                                    <span className={styles.label}>Коментар:</span>{" "}
                                    {t.description}
                                </div>
                            )}

                        </div>

                        {t.nextExecutionTime && (
                            <div className={styles.nextExecution}>
                                Наступне виконання:{" "}
                                <b>
                                    {formatDate(t.nextExecutionTime)}
                                </b>
                            </div>
                        )}

                        <div className={styles.actions}>

                            <Button
                                className={styles.actionButton}
                                onClick={() => makeTemplatePayment(t.id)}
                            >
                                Виконати
                            </Button>

                            <Button
                                variant="danger"
                                className={styles.actionButton}
                                onClick={() => deletePaymentTemplate(t.id)}
                            >
                                Видалити
                            </Button>

                        </div>

                    </div>
                ))}
            </div>

        </section>
    )
}