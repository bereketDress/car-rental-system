// Purpose:
// - Displays customer registration form.
// - Collects customer and address information.
// - Sends registration request to backend.
// - Shows success or error message.

import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext.jsx";

const emptyForm = {
    name: "",
    email: "",
    password: "",
    phone: "",
    licenseNumber: "",
    address: {
        street: "",
        city: "",
        state: "",
        zipcode: "",
        country: "",
    },
};

export default function RegisterPage() {
    const navigate = useNavigate();
    const { register } = useAuth();
    const [form, setForm] = useState(emptyForm);
    const [message, setMessage] = useState("");

    const handleChange = (e) => {
        const { name, value } = e.target;

        if (name.startsWith("address.")) {
            const field = name.split(".")[1];
            setForm({...form,
                address: { ...form.address, [field]: value },});
        } else {
            setForm({ ...form, [name]: value });
        }
    };

    const handleRegister = async (e) => {
        e.preventDefault();

        try {
            const payload = {
                role: "CUSTOMER",
                name: form.name,
                email: form.email,
                password: form.password,
                phone: form.phone,
                licenseNumber: form.licenseNumber,
                street: form.address.street,
                city: form.address.city,
                state: form.address.state,
                zipcode: form.address.zipcode,
                country: form.address.country,
            };

            await register(payload);
            navigate("/cars", { replace: true });
        } catch (err) {
            setMessage(err.message || "Registration failed. Please try again.");
        }
    };

    const inputClass = "w-full rounded border p-3";

    return (
        <div className="mx-auto my-10 max-w-2xl rounded-lg border bg-white p-8 shadow">
            <h1 className="mb-6 text-center text-3xl font-bold">
                Create Account
            </h1>

            {message && (
                <div className="mb-6 rounded bg-red-100 p-3 text-center text-red-700">
                    {message}
                </div>
            )}

            <form onSubmit={handleRegister} className="grid gap-4 md:grid-cols-2">
                <input
                    name="name"
                    placeholder="Full name"
                    value={form.name}
                    onChange={handleChange}
                    className={inputClass}
                    required
                />

                <input
                    name="email"
                    type="email"
                    placeholder="Email"
                    value={form.email}
                    onChange={handleChange}
                    className={inputClass}
                    required
                />

                <input
                    name="password"
                    type="password"
                    placeholder="Password"
                    value={form.password}
                    onChange={handleChange}
                    className={inputClass}
                    required
                />

                <input
                    name="phone"
                    placeholder="Phone"
                    value={form.phone}
                    onChange={handleChange}
                    className={inputClass}
                />

                <input
                    name="licenseNumber"
                    placeholder="License number"
                    value={form.licenseNumber}
                    onChange={handleChange}
                    className={`${inputClass} md:col-span-2`}
                />

                <input
                    name="address.street"
                    placeholder="Street"
                    value={form.address.street}
                    onChange={handleChange}
                    className={`${inputClass} md:col-span-2`}
                />

                {["city", "state", "zipcode", "country"].map((field) => (
                    <input
                        key={field}
                        name={`address.${field}`}
                        placeholder={field}
                        value={form.address[field]}
                        onChange={handleChange}
                        className={inputClass}
                    />
                ))}

                <button className="rounded bg-green-600 p-3 font-bold text-white hover:bg-green-700 md:col-span-2">
                    Register
                </button>
            </form>

            <p className="mt-6 text-center text-sm">
                Already have an account?{" "}
                <Link to="/login" className="font-semibold text-blue-600">
                    Sign in
                </Link>
            </p>
        </div>
    );
}
