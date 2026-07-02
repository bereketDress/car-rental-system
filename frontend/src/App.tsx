import { Routes, Route } from "react-router-dom";

import { AuthProvider } from "./context/AuthContext";

import Navbar from "./components/navabar/Navbar";
import ProtectedRoute from "./components/route/ProtectedRoute";

import HomePage from "./pages/home/HomePage";
import CarsPage from "./pages/car/CarsPage";
import LoginPage from "./pages/login/LoginPage";
import RegisterPage from "./pages/register/RegisterPage";
import ReservationsPage from "./pages/reservation/ReservationsPage";
import AdminCarsPage from "./pages/car/AdminCarsPage";
import BranchesPage from "./pages/branch/BranchesPage";
import PaymentsPage from "./pages/payment/PaymentsPage";
import DamagePage from "./pages/damage/DamagePage";
import ReportsPage from "./pages/report/ReportsPage";
import StaffPage from "./pages/staff/StaffPage";
import Chatbot from "./components/chatbot/Chatbot";

export default function App() {
    return (
        <AuthProvider>
            <Navbar />

            <Routes>
                {/* Public routes */}
                <Route path="/" element={<HomePage />} />
                <Route path="/cars" element={<CarsPage />} />
                <Route path="/login" element={<LoginPage />} />
                <Route path="/register" element={<RegisterPage />} />

                {/* Protected routes */}
                <Route element={<ProtectedRoute allowedRoles={["CUSTOMER", "STAFF", "MANAGER"]} />}>
                    <Route path="/reservations" element={<ReservationsPage />} />
                    <Route path="/payments" element={<PaymentsPage />} />
                </Route>

                <Route element={<ProtectedRoute allowedRoles={["STAFF", "MANAGER"]} />}>
                    <Route path="/damages" element={<DamagePage />} />
                </Route>

                <Route element={<ProtectedRoute allowedRoles={["MANAGER"]} />}>
                    <Route path="/manage/cars" element={<AdminCarsPage />} />
                    <Route path="/staff" element={<StaffPage />} />
                    <Route path="/branches" element={<BranchesPage />} />
                    <Route path="/reports" element={<ReportsPage />} />
                </Route>
            </Routes>
            {/* Chatbot appears on every page */}
            <Chatbot />
        </AuthProvider>
    );
}
