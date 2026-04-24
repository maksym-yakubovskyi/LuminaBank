import { Input } from "@/presentation/ui/input/Input"
import { Select } from "@/presentation/ui/select/Select"
import styles from "./RecurrenceFields.module.css"

type Props = {
    register: any
    watch: any
    errors: any
    path: string // "recurrence"
}

export function RecurrenceFields({ register, watch, errors, path }: Props) {
    const enabled = watch(`${path}.enabled`)
    const type = watch(`${path}.type`)

    const recurrenceErrors = errors?.[path]

    return (
        <div className={styles.container}>

            {/* ENABLE CHECKBOX */}
            <div className={styles.checkboxRow}>
                <input
                    type="checkbox"
                    {...register(`${path}.enabled`)}
                />
                <span>Регулярний платіж</span>
            </div>

            {enabled && (
                <div className={styles.section}>

                    {/* TYPE */}
                    <Select
                        label="Періодичність"
                        {...register(`${path}.type`)}
                        error={recurrenceErrors?.type?.message}
                    >
                        <option value="">Оберіть</option>
                        <option value="DAILY">Щодня</option>
                        <option value="WEEKLY">Щотижня</option>
                        <option value="MONTHLY">Щомісяця</option>
                    </Select>

                    {/* TIME */}
                    <div>
                        <label>Час виконання</label>

                        <div className={styles.timeRow}>
                            <Input
                                type="number"
                                min={0}
                                max={23}
                                placeholder="Год (0-23)"
                                className={styles.timeInput}
                                {...register(`${path}.hour`, { valueAsNumber: true })}
                            />

                            <Input
                                type="number"
                                min={0}
                                max={59}
                                placeholder="Хв (0-59)"
                                className={styles.timeInput}
                                {...register(`${path}.minute`, { valueAsNumber: true })}
                            />
                        </div>

                        {(recurrenceErrors?.hour || recurrenceErrors?.minute) && (
                            <div className={styles.errorText}>
                                Вкажіть коректний час
                            </div>
                        )}
                    </div>

                    {/* WEEKLY */}
                    {type === "WEEKLY" && (
                        <Select
                            label="День тижня"
                            {...register(`${path}.dayOfWeek`)}
                            error={recurrenceErrors?.dayOfWeek?.message}
                        >
                            <option value="">Оберіть</option>
                            <option value="MON">Понеділок</option>
                            <option value="TUE">Вівторок</option>
                            <option value="WED">Середа</option>
                            <option value="THU">Четвер</option>
                            <option value="FRI">Пʼятниця</option>
                            <option value="SAT">Субота</option>
                            <option value="SUN">Неділя</option>
                        </Select>
                    )}

                    {/* MONTHLY */}
                    {type === "MONTHLY" && (
                        <Input
                            type="number"
                            min={1}
                            max={28}
                            label="День місяця (1-28)"
                            {...register(`${path}.dayOfMonth`, { valueAsNumber: true })}
                            error={recurrenceErrors?.dayOfMonth?.message}
                        />
                    )}

                </div>
            )}

        </div>
    )
}