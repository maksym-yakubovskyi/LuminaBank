import type {BusinessCategory} from "@/features/enum/enum.ts";

export interface Provider {
    id: number;
    companyName: string;
    category: BusinessCategory;
}
