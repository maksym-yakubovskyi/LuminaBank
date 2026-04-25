import {createBrowserRouter, Navigate} from "react-router-dom";
import LoginPage from "@/presentation/pages/auth/LoginPage.tsx";
import RegisterPage from "@/presentation/pages/auth/RegisterPage.tsx";
import App from "@/App.tsx";
import {AuthLayouts} from "@/app/layouts/auth-layout.tsx";
import {MainLayout} from "@/app/layouts/main-layout.tsx";
import DashboardPage from "@/presentation/pages/dashboard/DashboardPage.tsx";
import {ProtectedRoute} from "@/app/router/protected-route.tsx";
import TransactionHistoryPage from "@/presentation/pages/payment/TransactionHistoryPage.tsx";
import AccountsPage from "@/presentation/pages/account/AccountsPage.tsx";
import UserProfilePage from "@/presentation/pages/profile/UserProfilePage.tsx";
import {PublicRoute} from "@/app/router/public-route.tsx";
import PaymentsPage from "@/presentation/pages/payment/PaymentsPage.tsx";
import AnalyticsPage from "@/presentation/pages/analytics/AnalyticsPage.tsx";
import LoanPage from "@/presentation/pages/loan/LoanPage.tsx";
import {RoleProtectedRoute} from "@/app/router/role-protected-route.tsx";
import OperatorPage from "@/presentation/pages/operator/OperatorPage.tsx";

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
                    {
                        path: "payment",
                        element: (
                            <ProtectedRoute>
                                <PaymentsPage />
                            </ProtectedRoute>
                        )
                    },
                    {
                        path: "analytics",
                        element: (
                            <ProtectedRoute>
                                <AnalyticsPage />
                            </ProtectedRoute>
                        )
                    },
                    {
                        path: "loan",
                        element: (
                            <ProtectedRoute>
                                <LoanPage />
                            </ProtectedRoute>
                        )
                    },
                    {
                        path: "operator",
                        element: (
                            <RoleProtectedRoute>
                                <OperatorPage />
                            </RoleProtectedRoute>
                        )
                    }
                ]
            },
            {
                index:true,
                element:<Navigate to="/dashboard" replace/>
            }
        ]
    },
])