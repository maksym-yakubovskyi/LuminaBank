import type {BusinessCategory} from "@/domain/business/business-category.enum.ts";

export interface Provider {
    id: number;
    companyName: string;
    category: BusinessCategory;
}
