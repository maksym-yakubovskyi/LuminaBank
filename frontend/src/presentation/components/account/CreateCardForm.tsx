import type {Account} from "@/domain/account/account.types.ts";
import {z} from "zod"
import {useForm} from "react-hook-form"
import {zodResolver} from "@hookform/resolvers/zod"
import AccountService from "@/application/account/account.service.ts";
import CardService from "@/application/account/card.service.ts";
import {Button} from "@/presentation/ui/button/Button.tsx";
import {useMemo} from "react";
import {useAuth} from "@/application/auth/auth.context.tsx";
import { AccountType } from "@/domain/account/account-type.enum";
import {CardType} from "@/domain/card/card-type.enum.ts";
import {CardNetwork} from "@/domain/card/card-network.enum.ts";
import {Currency} from "@/domain/shared/currency.enum.ts";
import styles from "./CreateCardForm.module.css"
import { Select } from "@/presentation/ui/select/Select"
import { Input } from "@/presentation/ui/input/Input"

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
            className={styles.container}
        >
            <h3 className={styles.title}>
                Відкриття нової картки
            </h3>

            {/* EXISTING ACCOUNT */}
            <div className={styles.section}>
                <Select
                    label="Існуючий рахунок"
                    {...register("accountId", { valueAsNumber: true })}
                >
                    <option value={-1}>
                        + Створити новий рахунок
                    </option>

                    {accounts.map(a => (
                        <option key={a.id} value={a.id}>
                            {a.iban} ({a.currency})
                        </option>
                    ))}
                </Select>
            </div>

            {/* NEW ACCOUNT */}
            {selectedAccountId === -1 && (
                <>
                    <div className={styles.divider} />

                    <div className={styles.section}>
                        <h4 className={styles.sectionTitle}>
                            Новий рахунок
                        </h4>

                        <Select
                            label="Валюта"
                            error={errors.currency?.message}
                            {...register("currency")}
                        >
                            {Object.values(Currency).map(cur => (
                                <option key={cur} value={cur}>
                                    {cur}
                                </option>
                            ))}
                        </Select>

                        <Select
                            label="Тип рахунку"
                            error={errors.accountType?.message}
                            {...register("accountType")}
                        >
                            {allowedAccountTypes.map(type => (
                                <option key={type} value={type}>
                                    {type}
                                </option>
                            ))}
                        </Select>
                    </div>
                </>
            )}

            <div className={styles.divider} />

            {/* CARD SETTINGS */}
            <div className={styles.section}>

                <Select
                    label="Тип картки"
                    error={errors.cardType?.message}
                    {...register("cardType")}
                >
                    {Object.values(CardType).map(type => (
                        <option key={type} value={type}>
                            {type}
                        </option>
                    ))}
                </Select>

                <Select
                    label="Платіжна система"
                    error={errors.cardNetwork?.message}
                    {...register("cardNetwork")}
                >
                    {Object.values(CardNetwork).map(network => (
                        <option key={network} value={network}>
                            {network}
                        </option>
                    ))}
                </Select>

                <Input
                    type="number"
                    label="Ліміт"
                    min={0}
                    error={errors.limit?.message}
                    {...register("limit", { valueAsNumber: true })}
                />
            </div>

            {/* ACTIONS */}
            <div className={styles.actions}>
                <Button
                    type="button"
                    onClick={onCancel}
                >
                    Скасувати
                </Button>

                <Button
                    type="submit"
                    loading={isSubmitting}
                >
                    Створити
                </Button>
            </div>
        </form>
    )
}