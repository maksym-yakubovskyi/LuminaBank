import axios from "axios"

export function extractErrorMessage(error: unknown): string {
    if (axios.isAxiosError(error)) {
        return (
            error.response?.data?.message ??
            error.response?.data?.error ??
            "Невідома помилка сервера"
        )
    }

    if (error instanceof Error) {
        return error.message
    }

    return "Невідома помилка"
}