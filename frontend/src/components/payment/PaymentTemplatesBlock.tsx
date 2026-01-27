import type {TemplateItem} from "@/features/types/template.ts";
import PaymentService from "@/api/service/PaymentService.ts";
import {PaymentTemplateType} from "@/features/enum/enum.ts";
import {extractErrorMessage} from "@/api/apiError.ts";

interface Props {
templates: TemplateItem[]
    onDeleted: (id: number) => void
}

export function PaymentTemplatesBlock ({templates,onDeleted}:Props){
    if (!templates) return <>Завантаження...</>
    if (templates.length === 0) return <>Список шаблонів порожній</>

    async function deletePaymentTemplate(id: number) {
        try {
            await PaymentService.deletePaymentTemplate(id)
            onDeleted(id)
        } catch (err) {
            console.error(err)
            const message = extractErrorMessage(err)
            alert("Помилка видалення: " + message)
        }
    }

    async function makeTemplatePayment(id: number){
        try{
            await PaymentService.makeTemplatePayment(id)
        } catch (err: any) {
            console.error(err)
            const message = extractErrorMessage(err)
            alert("Помилка відправки" + message)
        }
    }

    return (
        <>
            <h3 style={{ marginBottom: "12px" }}>Шаблони</h3>
            <div style={{ display: "flex", flexDirection: "column", gap: "12px" }}>
                {templates.map((t) => (
                    <div
                        key={t.id}
                        style={{
                            border: "1px solid #eee",
                            borderRadius: "10px",
                            padding: "12px",
                            background: "#fff",
                        }}
                    >
                        <div style={{ display: "flex", justifyContent: "space-between", gap: "10px" }}>
                            <div>
                                <div style={{ fontWeight: 700 }}>{t.name}</div>
                                <div style={{ fontSize: "12px", color: "#666" }}>
                                    {t.type === PaymentTemplateType.TRANSFER ? "Переказ" : "Оплата послуги"}
                                </div>
                            </div>

                            <div style={{ textAlign: "right" }}>
                                <div style={{ fontWeight: 700 }}>{t.amount} ₴</div>
                                <div style={{ fontSize: "12px", color: t.isRecurring ? "#0a7" : "#888" }}>
                                    {t.isRecurring ? "Регулярний" : "Разовий"}
                                </div>
                            </div>
                        </div>

                        {/* body */}
                        <div style={{ marginTop: "10px", fontSize: "14px" }}>
                            <div>
                                <span style={{ color: "#666" }}>З карти:</span>{" "}
                                <b>{t.fromCardNumber}</b>
                            </div>

                            {t.type === PaymentTemplateType.TRANSFER && (
                                <div>
                                    <span style={{ color: "#666" }}>На карту:</span>{" "}
                                    <b>{t.toCardNumber}</b>
                                </div>
                            )}

                            {t.description && (
                                <div style={{ marginTop: "6px" }}>
                                    <span style={{ color: "#666" }}>Коментар:</span> {t.description}
                                </div>
                            )}
                        </div>

                        {t.nextExecutionTime && (
                            <div style={{ marginTop: "10px", fontSize: "12px", color: "#666" }}>
                                <div>
                                    Наступне виконання:{" "}
                                    <b>{new Date(t.nextExecutionTime).toLocaleString("uk-UA")}</b>
                                </div>
                            </div>
                        )}

                        {/* actions */}
                        <div style={{ marginTop: "12px", display: "flex", gap: "8px" }}>
                            <button
                                type="button"
                                style={{
                                    padding: "6px 10px",
                                    borderRadius: "8px",
                                    border: "1px solid #ddd",
                                    background: "#fafafa",
                                    cursor: "pointer",
                                }}
                                onClick={() => makeTemplatePayment(t.id)}
                            >
                                Виконати
                            </button>

                            <button
                                type="button"
                                style={{
                                    padding: "6px 10px",
                                    borderRadius: "8px",
                                    border: "1px solid #ddd",
                                    background: "#fff5f5",
                                    cursor: "pointer",
                                }}
                                onClick={() => deletePaymentTemplate(t.id)}
                            >
                                Видалити
                            </button>
                        </div>
                    </div>
                ))}
            </div>
        </>
    )
}