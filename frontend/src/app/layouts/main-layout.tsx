import {Outlet} from "react-router-dom";
import {Header} from "@/presentation/ui/header/Header.tsx";
import styles from "./MainLayout.module.css";

export function MainLayout() {
    return (
        <div className={styles.layout}>

            <Header />

            <main className={styles.main}>
                <Outlet />
            </main>

        </div>
    )
}