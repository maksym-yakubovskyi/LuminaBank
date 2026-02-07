export interface BlockState<T> {
    isLoading: boolean
    data: T | null
}