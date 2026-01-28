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
        <div style={{ marginTop: "12px" }}>
            <label style={{ display: "flex", gap: "8px", alignItems: "center" }}>
                <input type="checkbox" {...register(`${path}.enabled`)} />
                Регулярний платіж
            </label>

            {enabled && (
                <>
                    <div style={{ marginTop: "10px" }}>
                        <label>Періодичність</label>
                        <select {...register(`${path}.type`)} style={{ display: "block", width: "100%" }}>
                            <option value="">Оберіть</option>
                            <option value="DAILY">Щодня</option>
                            <option value="WEEKLY">Щотижня</option>
                            <option value="MONTHLY">Щомісяця</option>
                        </select>
                        {recurrenceErrors?.type && (
                            <p style={{ color: "red" }}>{recurrenceErrors.type.message}</p>
                        )}
                    </div>

                    <div style={{ marginTop: "10px" }}>
                        <label>Час</label>
                        <div style={{ display: "flex", gap: "8px" }}>
                            <input
                                type="number"
                                min={0}
                                step={1}
                                max={23}
                                placeholder="год (0-23)"
                                {...register(`${path}.hour`, { valueAsNumber: true })}
                                style={{ width: "50%" }}
                            />
                            <input
                                type="number"
                                min={0}
                                step={1}
                                max={59}
                                placeholder="хв (0-59)"
                                {...register(`${path}.minute`, { valueAsNumber: true })}
                                style={{ width: "50%" }}
                            />
                        </div>

                        {(recurrenceErrors?.hour || recurrenceErrors?.minute) && (
                            <p style={{ color: "red" }}>Вкажіть коректний час</p>
                        )}
                    </div>

                    {type === "WEEKLY" && (
                        <div style={{ marginTop: "10px" }}>
                            <label>День тижня</label>
                            <select {...register(`${path}.dayOfWeek`)} style={{ display: "block", width: "100%" }}>
                                <option value="">Оберіть</option>
                                <option value="MON">Пн</option>
                                <option value="TUE">Вт</option>
                                <option value="WED">Ср</option>
                                <option value="THU">Чт</option>
                                <option value="FRI">Пт</option>
                                <option value="SAT">Сб</option>
                                <option value="SUN">Нд</option>
                            </select>
                            {recurrenceErrors?.dayOfWeek && (
                                <p style={{ color: "red" }}>{recurrenceErrors.dayOfWeek.message}</p>
                            )}
                        </div>
                    )}

                    {type === "MONTHLY" && (
                        <div style={{ marginTop: "10px" }}>
                            <label>День місяця (1-28)</label>
                            <input
                                type="number"
                                min={1}
                                max={28}
                                step={1}
                                {...register(`${path}.dayOfMonth`, { valueAsNumber: true })}
                                style={{ display: "block", width: "100%" }}
                            />
                            {recurrenceErrors?.dayOfMonth && (
                                <p style={{ color: "red" }}>{recurrenceErrors.dayOfMonth.message}</p>
                            )}
                        </div>
                    )}
                </>
            )}
        </div>
    )
}