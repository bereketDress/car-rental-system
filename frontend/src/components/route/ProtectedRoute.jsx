
// - Protect private pages.
// - If user is not logged in, redirect to Login.
// - After login, return the user to the original page.

import { Navigate, Outlet, useLocation } from "react-router-dom";
import { useAuth } from "../../context/AuthContext.jsx";

/**
 * @param {{ allowedRoles?: string[], allowedCapabilities?: string[] }} props
 */
export default function ProtectedRoute({ allowedRoles = undefined, allowedCapabilities = undefined }) {
    const { auth, isAuthenticated } = useAuth();
    const location = useLocation();

    if (!isAuthenticated) {
        return (<Navigate
                to="/login"
                replace
                state={{ from: location }}
            />
        );
    }

    if (allowedRoles?.length && !allowedRoles.includes(auth?.role)) {
        return <Navigate to="/cars" replace />;
    }

    if (allowedCapabilities?.length) {
        const capabilities = new Set(auth?.capabilities ?? []);
        const hasCapability = allowedCapabilities.some((capability) => capabilities.has(capability));

        if (!hasCapability) {
            return <Navigate to="/cars" replace />;
        }
    }

    return <Outlet />;
}
