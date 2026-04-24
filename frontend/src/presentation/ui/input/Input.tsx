import {
    forwardRef,
    type InputHTMLAttributes,
} from "react";

import styles from "./Input.module.css";

interface Props extends InputHTMLAttributes<HTMLInputElement> {
    label?: string;
    error?: string;
}

export const Input = forwardRef<HTMLInputElement, Props>(
    ({ label, error, className, ...props }, ref) => {
        return (
            <div className={styles.wrapper}>
                {label && (
                    <label className={styles.label}>{label}</label>
                )}

                <input
                    ref={ref}
                    className={`${styles.input} ${className ?? ""}`}
                    {...props}
                />

                {error && (
                    <p className={styles.error}>{error}</p>
                )}
            </div>
        );
    }
);

Input.displayName = "Input";