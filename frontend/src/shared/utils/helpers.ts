export function formatAmount(
    amount: number,
    currency: string
) {
    return new Intl.NumberFormat("uk-UA", {
        style: "currency",
        currency,
    }).format(amount)
}

export function formatDate(date: string) {
    return new Date(date).toLocaleString("uk-UA", {
        day: "2-digit",
        month: "short",
        hour: "2-digit",
        minute: "2-digit",
    })
}