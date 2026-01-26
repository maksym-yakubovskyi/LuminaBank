import type {BusinessCategoryType} from "@/features/enum/enum.ts";

export interface Provider {
    id: number;
    companyName: string;
    category: BusinessCategoryType;
}
