export interface Address {
    street: string
    city: string
    houseNumber: string
    zipCode: string
    country: string
}

export interface UserProfile {
    id: number
    firstName: string
    lastName: string
    email: string
    phoneNumber: string
    birthDate: string
    address: Address | null
    role: string
}

export interface UserUpdateDto {
    firstName: string
    lastName: string
    email: string
    phoneNumber: string
    birthDate: string
    street: string
    city: string
    houseNumber: string
    zipCode: string
    country: string
}
