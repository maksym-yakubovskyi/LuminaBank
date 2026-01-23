import type {Account} from "@/features/types/account.ts";
import {z} from "zod"
import {useForm} from "react-hook-form"
import {zodResolver} from "@hookform/resolvers/zod"
import AccountService from "@/api/service/AccountService.ts";
import CardService from "@/api/service/CardService.ts";
import {Button} from "@/components/button/Button.tsx";
import {AccountType, CardNetwork, CardType, Currency} from "@/features/enum/enum.ts";

const createCardSchema = z.object({
    cardType: z.enum([CardType.PHYSICAL, CardType.VIRTUAL], {
        message: "Оберіть тип картки",
    }),

    cardNetwork: z.enum([CardNetwork.VISA, CardNetwork.MASTERCARD], {
        message: "Оберіть платіжну систему",
    }),

    limit: z
        .number( "Ліміт має бути числом")
        .min(0, "Ліміт не може бути меншим за 0")
        .max(1_000_000, "Занадто великий ліміт"),

    currency: z.enum(
        [
            Currency.UAH,
            Currency.USD,
            Currency.EUR,
            Currency.GBP,
            Currency.PLN,
            Currency.CHF,
        ],
        { message: "Оберіть валюту" }
    ),

    accountType: z.enum([AccountType.DEBIT, AccountType.CREDIT], {
        message: "Оберіть тип рахунку",
    }),
})

type FormInputs = z.infer<typeof createCardSchema>

interface Props {
    account: Account | null
    onCancel: () => void
}

export function CreateCardForm({ account, onCancel }: Props) {
    const {
        register,
        handleSubmit,
        formState: {errors, isSubmitting },
    } = useForm<FormInputs>({
        resolver: zodResolver(createCardSchema),
        defaultValues: {
            cardType: CardType.PHYSICAL,
            cardNetwork: CardNetwork.VISA,
            limit: 0,
            currency: Currency.UAH,
            accountType: AccountType.DEBIT,
        },
    })

    const onSubmit = async (data: FormInputs) => {
        let accountId = account?.id

        if (!accountId) {
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

        onCancel()
    }

    return (
        <form
            onSubmit={handleSubmit(onSubmit)}
            style={{ display: "grid", gap: "12px" }}
        >
            <h3>Відкриття нової картки</h3>

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

            {!account && (
                <>
                    <h4>Рахунок</h4>

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
                            {Object.values(AccountType).map((type) => (
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