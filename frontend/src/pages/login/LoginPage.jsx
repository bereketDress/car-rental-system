
// - Displays login form.
// - Sends credentials to AuthContext login().
// - Redirects user after successful login.

import { useState } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext.jsx";

export default function LoginPage() {
    const navigate = useNavigate();
    const location = useLocation();
    const { login } = useAuth();

    const [form, setForm] = useState({
        email: "",
        password: "",
        role: "CUSTOMER",
    });

    const [error, setError] = useState("");

    const handleChange = (e) => {
        setForm({ ...form, [e.target.name]: e.target.value });
    };

    const handleLogin = async (e) => {
        e.preventDefault();
        setError("");

        try {
            const auth = await login(form);
            const defaultRoute = auth.role === "MANAGER"
                ? "/reports"
                : auth.role === "STAFF"
                    ? "/reservations"
                    : "/cars";
            const redirectTo = location.state?.from?.pathname || defaultRoute;
            navigate(redirectTo, { replace: true });
        } catch {
            setError("Invalid email, password, or role.");
        }
    };

    return (
        <div className="flex min-h-[calc(100vh-72px)] items-center justify-center bg-gray-50 p-4">
            <form
                onSubmit={handleLogin}
                className="w-full max-w-md rounded-lg border bg-white p-8 shadow"
            >
                <h1 className="mb-6 text-2xl font-bold">Sign in</h1>

                {error && (
                    <p className="mb-4 rounded bg-red-50 p-3 text-red-600">
                        {error}
                    </p>
                )}

                <input
                    name="email"
                    type="email"
                    placeholder="Email"
                    value={form.email}
                    onChange={handleChange}
                    className="mb-4 w-full rounded border p-3"
                    required
                />

                <input
                    name="password"
                    type="password"
                    placeholder="Password"
                    value={form.password}
                    onChange={handleChange}
                    className="mb-4 w-full rounded border p-3"
                    required
                />

                <select
                    name="role"
                    value={form.role}
                    onChange={handleChange}
                    className="mb-4 w-full rounded border p-3"
                >
                    <option value="CUSTOMER">Customer</option>
                    <option value="STAFF">Staff</option>
                    <option value="MANAGER">Manager</option>
                </select>

                <button className="w-full rounded bg-blue-600 p-3 font-semibold text-white hover:bg-blue-700">
                    Sign in
                </button>

                <p className="mt-6 text-center text-sm">
                    Don't have an account?{" "}
                    <Link to="/register" className="font-semibold text-blue-600">
                        Sign up
                    </Link>
                </p>
            </form>
        </div>
    );
}
