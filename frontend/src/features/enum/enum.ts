export const UserType = {
    INDIVIDUAL_USER: "INDIVIDUAL_USER",
    BUSINESS_USER: "BUSINESS_USER",
} as const;

export type UserType = typeof UserType[keyof typeof UserType];