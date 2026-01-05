import {Outlet} from "react-router-dom";
import {Header} from "@/components/header/Header.tsx";

export function MainLayout() {
    return (
        <div style={{ minHeight: "100vh" }}>
            <Header />

            <main
                style={{
                    display: "grid",
                    gridTemplateColumns: "1fr 2fr",
                    gap: "16px",
                    padding: "16px",
                }}
            >
                <Outlet />
            </main>
        </div>
    )
}