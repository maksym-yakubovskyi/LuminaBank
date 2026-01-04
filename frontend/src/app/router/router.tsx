import {createBrowserRouter} from "react-router-dom";
import LoginPage from "@/pages/login/LoginPage.tsx";
import RegisterPage from "@/pages/register/RegisterPage.tsx";
import App from "@/app/App.tsx";
import {AuthLayouts} from "@/app/layouts/AuthLayouts.tsx";
import {publicLoader} from "@/app/router/public.loader.tsx";
import {MainLayout} from "@/app/layouts/MainLayouts.tsx";
import {protectedLoader} from "@/app/router/protected.loader.tsx";
import DashboardPage from "@/pages/dashboard/DashboardPage.tsx";
import {rootLoader} from "@/app/router/rootLoader.tsx";

export const router = createBrowserRouter([
    {
        path: "/",
        loader: rootLoader
    },
    {
        element: <App />,
        children: [
            {
                element: <AuthLayouts/>,
                children: [
                    {
                        path: "login",
                        loader: publicLoader,
                        element: <LoginPage/>,
                    },
                    {
                        path: "register",
                        loader: publicLoader,
                        element: <RegisterPage/>,
                    }
                ]
            },
            {
                element: <MainLayout/>,
                children: [
                    {
                        path: "dashboard",
                        loader: protectedLoader,
                        element:<DashboardPage/>,
                    },
                ]
            }
        ]
    },
])