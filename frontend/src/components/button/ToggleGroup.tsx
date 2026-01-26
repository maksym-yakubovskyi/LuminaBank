import { Button } from "@/components/button/Button.tsx";

type ToggleOption<T extends string> = {
    value: T;
    label: string;
};

interface Props<T extends string> {
    value: T;
    options: ToggleOption<T>[];
    onChange: (value: T) => void;
}

export function ToggleGroup<T extends string>({ value, options, onChange }: Props<T>) {
    return (
        <div style={{ display: "flex", gap: "8px" }}>
            {options.map((opt) => (
                <Button
                    key={opt.value}
                    type="button"
                    disabled={value === opt.value}
                    onClick={() => onChange(opt.value)}
                >
                    {opt.label}
                </Button>
            ))}
        </div>
    );
}