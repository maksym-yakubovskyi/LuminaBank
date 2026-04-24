import type {BusinessCategory} from "@/domain/business/business-category.enum.ts";

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

export interface BusinessProfile {
    id: number
    companyName: string
    email: string
    phoneNumber: string
    adrpou: string
    description: string | null
    category: BusinessCategory
    address: Address | null
    role: string
}

export interface BusinessUserUpdateDto {
    companyName: string
    email: string
    phoneNumber: string
    adrpou: string
    description: string
    category: BusinessCategory
    street: string
    city: string
    houseNumber: string
    zipCode: string
    country: string
}