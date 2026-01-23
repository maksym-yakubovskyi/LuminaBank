import {createBrowserRouter, Navigate} from "react-router-dom";
import LoginPage from "@/pages/login/LoginPage.tsx";
import RegisterPage from "@/pages/register/RegisterPage.tsx";
import App from "@/app/App.tsx";
import {AuthLayouts} from "@/app/layouts/AuthLayouts.tsx";
import {MainLayout} from "@/app/layouts/MainLayouts.tsx";
import DashboardPage from "@/pages/dashboard/DashboardPage.tsx";
import {ProtectedRoute} from "@/app/router/ProtectedRoute.tsx";
import TransactionHistoryPage from "@/pages/transaction_history/TransactionHistoryPage.tsx";
import AccountsPage from "@/pages/account/AccountsPage.tsx";
import UserProfilePage from "@/pages/profile/UserProfilePage.tsx";
import {PublicRoute} from "@/app/router/PublicRouter.tsx";

export const router = createBrowserRouter([
    {
        path: "/",
        element: <App />,
        children: [
            {
                element: <AuthLayouts/>,
                children: [
                    {
                        path: "login",
                        element:(
                        <PublicRoute>
                            <LoginPage/>
                        </PublicRoute>
                        ),
                    },
                    {
                        path: "register",
                        element:(
                            <PublicRoute>
                                <RegisterPage/>
                            </PublicRoute>
                        ),
                    }
                ]
            },
            {
                element: <MainLayout/>,
                children: [
                    {
                        path: "dashboard",
                        element: (
                            <ProtectedRoute>
                                <DashboardPage />
                            </ProtectedRoute>
                        )
                    },
                    {
                        path: "transactions",
                        element: (
                            <ProtectedRoute>
                                <TransactionHistoryPage />
                            </ProtectedRoute>
                        ),
                    },
                    {
                        path: "transactions/:paymentId",
                        element: (
                            <ProtectedRoute>
                                <TransactionHistoryPage />
                            </ProtectedRoute>
                        ),
                    },
                    {
                        path: "accounts",
                        element: (
                            <ProtectedRoute>
                                <AccountsPage />
                            </ProtectedRoute>
                        ),
                    },
                    {
                        path: "accounts/:cardId",
                        element: (
                            <ProtectedRoute>
                                <AccountsPage />
                            </ProtectedRoute>
                        ),
                    },
                    {
                        path: "profile",
                        element: (
                            <ProtectedRoute>
                                <UserProfilePage />
                            </ProtectedRoute>
                        ),
                    },
                ]
            },
            {
                index:true,
                element:<Navigate to="/dashboard" replace/>
            }
        ]
    },
])