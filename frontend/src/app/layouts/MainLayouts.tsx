import {Outlet} from "react-router-dom";

export function MainLayout() {
    return (
        <div>
            <header>
                Header
            </header>
            <main><Outlet/></main>
        </div>
    )
}