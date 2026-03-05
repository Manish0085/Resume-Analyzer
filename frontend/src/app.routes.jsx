import { createBrowserRouter } from "react-router-dom";
import Login from "./features/auth/pages/Login.jsx";
import Register from "./features/auth/pages/Register.jsx";
import AuthSuccess from "./features/auth/pages/AuthSuccess.jsx";
import Protected from "./features/auth/components/Protected.jsx";
import Dashboard from "./features/ai/pages/Dashboard.jsx";
import GenerateReport from "./features/ai/pages/GenerateReport.jsx";
import ReportDetails from "./features/ai/pages/ReportDetails.jsx";
import LandingPage from "./features/ai/pages/LandingPage.jsx";

export const router = createBrowserRouter([
    {
        path: "/home",
        element: <LandingPage />
    },
    {
        path: "/login",
        element: <Login />
    },
    {
        path: "/register",
        element: <Register />
    },
    {
        path: "/auth/success",
        element: <AuthSuccess />
    },
    {
        path: "/",
        element: (
            <Protected>
                <Dashboard />
            </Protected>
        )
    },
    {
        path: "/ai/generate",
        element: (
            <Protected>
                <GenerateReport />
            </Protected>
        )
    },
    {
        path: "/ai/report/:reportId",
        element: (
            <Protected>
                <ReportDetails />
            </Protected>
        )
    }
])