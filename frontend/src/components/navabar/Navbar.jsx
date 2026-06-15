
// - Display navigation links.
// - Show links based on user role.
// - Allow login/logout.

import { Link, NavLink } from "react-router-dom";
import { useAuth } from "../../context/AuthContext.jsx";

export default function Navbar() {
    const { auth, isAuthenticated, logout } = useAuth();

    const isManager = auth?.role === "MANAGER";
    const isStaff = auth?.role === "STAFF" || isManager;
    const isCustomer = auth?.role === "CUSTOMER";
    const capabilities = new Set(auth?.capabilities ?? []);
    const can = (...items) => items.some((item) => capabilities.has(item));
    const canUseReservations = isAuthenticated && (
        can("MAKE_RESERVATION", "VIEW_BOOKING_HISTORY", "VIEW_RESERVATIONS")
        || isStaff
    );
    const canUsePayments = isAuthenticated && (
        can("PROCESS_PAYMENT", "PROCESS_CUSTOMER_PAYMENT")
        || isCustomer
        || isManager
    );

    const links = [
        { to: "/", label: "Home" },
        { to: "/cars", label: "Cars" },
        canUseReservations && { to: "/reservations", label: isCustomer ? "My Reservations" : "Reservations" },
        canUsePayments && { to: "/payments", label: isCustomer ? "My Payments" : "Payments" },

        !isAuthenticated && { to: "/register", label: "Register" },

        (isManager || can("MANAGE_VEHICLE", "MANAGE_VEHICLES")) && { to: "/manage/cars", label: "Manage Vehicles" },
        (isManager || can("MANAGE_STAFF")) && { to: "/staff", label: "Manage Staff" },
        isManager && { to: "/branches", label: "Branches" },

        (isStaff || can("VIEW_DAMAGES", "RECORD_DAMAGE")) && { to: "/damages", label: "Damages" },

        (isManager || can("VIEW_REPORT", "VIEW_REPORTS")) && { to: "/reports", label: "Reports" },
    ].filter(Boolean);

    return (
        <nav className="flex items-center justify-between bg-gray-800 px-6 py-4 text-white">
            <Link to="/" className="text-xl font-bold">
                CRMS
            </Link>

            <div className="flex gap-5">
                {links.map((link) => (
                    <NavLink
                        key={link.to}
                        to={link.to}
                        className={({ isActive }) =>
                            isActive ? "font-bold text-blue-300" : "hover:text-blue-300"
                        }
                    >
                        {link.label}
                    </NavLink>
                ))}
            </div>

            <div className="flex items-center gap-3">
                {isAuthenticated && (
                    <span className="text-sm">
            {auth.name || auth.role}
          </span>
                )}

                {isAuthenticated ? (
                    <button
                        onClick={logout}
                        className="rounded bg-red-600 px-3 py-2 hover:bg-red-700"
                    >
                        Logout
                    </button>
                ) : (
                    <Link
                        to="/login"
                        className="rounded bg-blue-600 px-3 py-2 hover:bg-blue-700"
                    >
                        Login
                    </Link>
                )}
            </div>
        </nav>
    );
}
