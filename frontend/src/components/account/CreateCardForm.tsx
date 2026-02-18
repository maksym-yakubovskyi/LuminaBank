import type {Account} from "@/features/types/account.ts";
import {z} from "zod"
import {useForm} from "react-hook-form"
import {zodResolver} from "@hookform/resolvers/zod"
import AccountService from "@/api/service/AccountService.ts";
import CardService from "@/api/service/CardService.ts";
import {Button} from "@/components/button/Button.tsx";
import {AccountType, CardNetwork, CardType, Currency} from "@/features/enum/enum.ts";
import {useMemo} from "react";
import {useAuth} from "@/features/auth/auth.context.tsx";

const getAllowedAccountTypes = (role?: string): AccountType[] => {
    if (role === "BUSINESS_USER") {
        return [AccountType.MERCHANT, AccountType.CREDIT];
    }

    return [AccountType.DEBIT, AccountType.CREDIT];
};
const createCardSchema = (role?: string) => {
    const allowedTypes = getAllowedAccountTypes(role);

    return z.object({
        accountId: z.number().optional(),

        cardType: z.enum(CardType, {message: "Оберіть тип картки",}),

        cardNetwork: z.enum(CardNetwork, {message: "Оберіть платіжну систему",}),

        limit: z
            .number("Ліміт має бути числом")
            .min(0, "Ліміт не може бути меншим за 0")
            .max(1_000_000, "Занадто великий ліміт"),

        currency: z.enum(Currency, { message: "Оберіть валюту" }),

        accountType: z.enum(
            allowedTypes as [AccountType, ...AccountType[]],
            {
                message: "Оберіть тип рахунку",
            }
        ),
    });
};

type FormInputs = z.infer<ReturnType<typeof createCardSchema>>;

interface Props {
    accounts: Account[]
    onCreated: () => void
    onCancel: () => void
}

export function CreateCardForm({ accounts,onCreated, onCancel }: Props) {
    const { user } = useAuth();

    const schema = useMemo(
        () => createCardSchema(user?.role),
        [user?.role]
    );

    const allowedAccountTypes = useMemo(
        () => getAllowedAccountTypes(user?.role),
        [user?.role]
    );

    const {
        register,
        handleSubmit,
        watch,
        formState: {errors, isSubmitting },
    } = useForm<FormInputs>({
        resolver: zodResolver(schema),
        defaultValues: {
            cardType: CardType.PHYSICAL,
            cardNetwork: CardNetwork.VISA,
            limit: 0,
            currency: Currency.UAH,
            accountType: allowedAccountTypes[0],
        },
    })

    const selectedAccountId = watch("accountId")

    const onSubmit = async (data: FormInputs) => {
        try {
            let accountId = data.accountId

            if (accountId === -1) {
                const createdAccount = await AccountService.createAccount({
                    currency: data.currency,
                    type: data.accountType,
                })
                accountId = createdAccount.id
            }

            await CardService.createCard(accountId!, {
                cardType: data.cardType,
                cardNetwork: data.cardNetwork,
                limit: data.limit,
            })

            onCreated()
        }catch (e) {
            console.error("Create card failed", e)
            alert("Не вдалося створити картку")
        }
    }

    return (
        <form
            onSubmit={handleSubmit(onSubmit)}
            style={{ display: "grid", gap: "12px" }}
        >
            <h3>Відкриття нової картки</h3>

                <div>
                    <label>Існуючий рахунок</label>
                    <select {...register("accountId", { valueAsNumber: true })}>
                        <option value={-1}>➕ Створити новий рахунок</option>
                        {accounts.map(a => (
                            <option key={a.id} value={a.id}>
                                {a.iban} ({a.currency})
                            </option>
                        ))}
                    </select>
                </div>

            {selectedAccountId === -1 && (
                <>
                    <h4>Новий рахунок</h4>

                    <div>
                        <label>Валюта</label>
                        <select {...register("currency")}>
                            {Object.values(Currency).map((cur) => (
                                <option key={cur} value={cur}>
                                    {cur}
                                </option>
                            ))}
                        </select>
                        {errors.currency && (
                            <p style={{ color: "red" }}>{errors.currency.message}</p>
                        )}
                    </div>

                    <div>
                        <label>Тип рахунку</label>
                        <select {...register("accountType")}>
                            {allowedAccountTypes.map((type) => (
                                <option key={type} value={type}>
                                    {type}
                                </option>
                            ))}
                        </select>
                        {errors.accountType && (
                            <p style={{ color: "red" }}>{errors.accountType.message}</p>
                        )}
                    </div>
                </>
            )}

            <div>
                <label>Тип картки</label>
                <select {...register("cardType")}>
                    {Object.values(CardType).map((type) => (
                        <option key={type} value={type}>
                            {type}
                        </option>
                    ))}
                </select>
                {errors.cardType && (
                    <p style={{ color: "red" }}>{errors.cardType.message}</p>
                )}
            </div>

            <div>
                <label>Платіжна система</label>
                <select {...register("cardNetwork")}>
                    {Object.values(CardNetwork).map((network) => (
                        <option key={network} value={network}>
                            {network}
                        </option>
                    ))}
                </select>
                {errors.cardNetwork && (
                    <p style={{ color: "red" }}>{errors.cardNetwork.message}</p>
                )}
            </div>

            <div>
                <label>Ліміт</label>
                <input type="number" {...register("limit", { valueAsNumber: true })} />
                {errors.limit && (
                    <p style={{ color: "red" }}>{errors.limit.message}</p>
                )}
            </div>

            <div style={{ display: "flex", gap: "8px" }}>
                <Button type="button" onClick={onCancel}>
                    Скасувати
                </Button>

                <Button type="submit" loading={isSubmitting}>
                    Створити
                </Button>
            </div>
        </form>
    )
}