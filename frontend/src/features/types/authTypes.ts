export interface LoginRequest {
    email: string
    password: string
}

export interface LoginResponse {
    accessToken: string
    refreshToken: string
    tokenType: string
    expiresIn: number
}

export interface RegisterClientRequest {
    email: string
    password: string
    verificationCode: string
    firstName: string
    lastName: string
    phoneNumber: string
    birthDate: string
    userType: string
}

export interface RegisterBusinessRequest {
    email: string
    password: string
    verificationCode: string
    phoneNumber: string
    companyName: string
    adrpou: string
    userType: string
}


export interface User{
    id: string
    role: string
}

