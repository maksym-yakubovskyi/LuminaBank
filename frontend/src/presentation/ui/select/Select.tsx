import {
    forwardRef,
    type SelectHTMLAttributes,
    type ReactNode,
} from "react";

import styles from "./Select.module.css";

interface Props
    extends SelectHTMLAttributes<HTMLSelectElement> {

    label?: string;

    error?: string;

    children: ReactNode;

}

export const Select =
    forwardRef<HTMLSelectElement, Props>(
        (
            {
                label,
                error,
                className,
                children,
                ...props
            },
            ref
        ) => {

            return (

                <div className={styles.wrapper}>

                    {label && (
                        <label className={styles.label}>
                            {label}
                        </label>
                    )}

                    <select
                        ref={ref}
                        className={
                            `${styles.select} ${className ?? ""}`
                        }
                        {...props}
                    >
                        {children}
                    </select>

                    {error && (
                        <p className={styles.error}>
                            {error}
                        </p>
                    )}

                </div>

            );

        }
    );

Select.displayName = "Select";