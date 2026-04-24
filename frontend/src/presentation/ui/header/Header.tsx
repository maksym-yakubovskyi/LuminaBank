import {useNavigate} from "react-router-dom";
import {useEffect, useRef, useState} from "react";
import styles from "./Header.module.css";

export function Header() {

    const navigate = useNavigate();

    const [open, setOpen] = useState(false);

    const menuRef = useRef<HTMLDivElement>(null);

    useEffect(() => {
        function handleClick(e: MouseEvent) {
            if (
                menuRef.current &&
                !menuRef.current.contains(e.target as Node)
            ) {
                setOpen(false);
            }
        }

        document.addEventListener("mousedown", handleClick);

        return () =>
            document.removeEventListener(
                "mousedown",
                handleClick
            );
    }, []);

    return (
        <header className={styles.header}>

            {/* LOGO */}
            <h2
                className={styles.logo}
                onClick={() =>
                    navigate("/dashboard")
                }
            >
                Lumina Bank
            </h2>

            {/* RIGHT SIDE */}
            <div
                className={styles.right}
                ref={menuRef}
            >

                {/* PROFILE ICON */}
                <div
                    className={styles.profile}
                    onClick={() =>
                        navigate("/profile")
                    }
                >
                    👤
                </div>

                {/* BURGER */}
                <button
                    className={styles.burger}
                    onClick={() =>
                        setOpen(!open)
                    }
                >
                    ☰
                </button>

                {/* DROPDOWN */}
                {open && (
                    <div className={styles.menu}>

                        <button
                            onClick={() =>
                                navigate("/payment")
                            }
                        >
                            Перекази та платежі
                        </button>

                        <button
                            onClick={() =>
                                navigate("/transactions")
                            }
                        >
                            Історія транзакцій
                        </button>

                        <button
                            onClick={() =>
                                navigate("/accounts")
                            }
                        >
                            Рахунки та картки
                        </button>

                        <button
                            onClick={() =>
                                navigate("/analytics")
                            }
                        >
                            Аналітика та звіти
                        </button>

                        <button
                            onClick={() =>
                                navigate("/loan")
                            }
                        >
                            Кредити
                        </button>

                    </div>
                )}

            </div>

        </header>
    )
}