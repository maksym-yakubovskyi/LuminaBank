import { Button } from "@/presentation/ui/button/Button.tsx";
import styles from "./ToggleGroup.module.css";

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
        <div className={styles.wrapper}>
            {options.map((opt: any) => (

                <Button
                    key={opt.value}
                    type="button"
                    className={styles.button}
                    disabled={value === opt.value}
                    onClick={() => onChange(opt.value)}
                >
                    {opt.label}
                </Button>

            ))}
        </div>
    );
}