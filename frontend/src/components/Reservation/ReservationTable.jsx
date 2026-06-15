
// - Show reservation list.
// - Confirm reservations.

import { CalendarDays, Check, LogOut, X } from "lucide-react";
import { rentalService, reservationService } from "../../services/api";
import { useState } from "react";

const statusStyles = {
    PENDING: "bg-amber-50 text-amber-700 ring-amber-200",
    CONFIRMED: "bg-blue-50 text-blue-700 ring-blue-200",
    CANCELLED: "bg-red-50 text-red-700 ring-red-200",
    COMPLETED: "bg-green-50 text-green-700 ring-green-200",
};

function StatusBadge({ status }) {
    return (
        <span className={`inline-flex rounded-full px-2.5 py-1 text-xs font-semibold ring-1 ring-inset ${statusStyles[status] || "bg-gray-50 text-gray-700 ring-gray-200"}`}>
            {status || "UNKNOWN"}
        </span>
    );
}

export default function ReservationTable({
                                             reservations,
                                             reload,
                                             role,
}) {
    const [message, setMessage] = useState("");
    const [checkoutReservationId, setCheckoutReservationId] = useState(null);
    const [checkoutForm, setCheckoutForm] = useState({
        startMileage: "",
        returnDate: "",
    });
    const isStaffOperator = role === "STAFF" || role === "MANAGER";
    const isCustomer = role === "CUSTOMER";

    const confirm = async (id) => {
        setMessage("");
        try {
            await reservationService.confirm(id);
            reload();
        } catch (error) {
            setMessage(error.message || "Confirm failed.");
        }
    };

    const cancel = async (id) => {
        setMessage("");
        try {
            await reservationService.cancel(id);
            reload();
        } catch (error) {
            setMessage(error.message || "Cancel failed.");
        }
    };

    const updateCheckout = (field, value) => {
        setCheckoutForm((current) => ({ ...current, [field]: value }));
    };

    const checkout = async (reservationId, e) => {
        e.preventDefault();
        setMessage("");

        try {
            await rentalService.checkout({
                reservationId: String(reservationId),
                startMileage: checkoutForm.startMileage,
                returnDate: checkoutForm.returnDate,
            });
            setCheckoutReservationId(null);
            setCheckoutForm({ startMileage: "", returnDate: "" });
            reload();
        } catch (error) {
            setMessage(error.message || "Check out failed.");
        }
    };

    return (
        <section className="rounded-lg border bg-white shadow-sm">
        <div className="flex flex-col gap-1 border-b p-5 md:flex-row md:items-center md:justify-between">
            <div>
                <h2 className="text-lg font-bold text-gray-950">
                    {isCustomer ? "Reservation History" : "Reservations"}
                </h2>
                <p className="text-sm text-gray-600">
                    {isCustomer ? "Your past and upcoming vehicle reservations." : "Booking requests and upcoming pickups."}
                </p>
            </div>

            <div className="inline-flex items-center gap-2 text-sm text-gray-500">
                <CalendarDays className="h-4 w-4" />
                {reservations.length} total
            </div>
        </div>

        {message && (
            <p className="mx-5 mt-5 rounded border border-red-200 bg-red-50 p-3 text-sm text-red-700">{message}</p>
        )}

        {!reservations.length ? (
            <div className="p-8 text-center text-gray-600">
                No reservations found.
            </div>
        ) : (
            <div className="overflow-x-auto">
                <table className="w-full min-w-[760px] text-left text-sm">
                    <thead className="bg-gray-50 text-xs uppercase text-gray-500">
                    <tr>
                        <th className="px-5 py-3 font-semibold">Customer</th>
                        <th className="px-5 py-3 font-semibold">Car</th>
                        <th className="px-5 py-3 font-semibold">Reserved</th>
                        <th className="px-5 py-3 font-semibold">Pickup</th>
                        <th className="px-5 py-3 font-semibold">Status</th>
                        <th className="px-5 py-3 text-right font-semibold">Actions</th>
                    </tr>
                    </thead>

                    <tbody className="divide-y divide-gray-100">
                    {reservations.map((r) => (
                        <tr key={r.reservationId} className="align-middle hover:bg-gray-50">
                            <td className="px-5 py-4 font-medium text-gray-950">
                                {r.customer?.name || "Unassigned"}
                            </td>
                            <td className="px-5 py-4 text-gray-700">
                                <div className="font-medium text-gray-950">
                                    {r.car?.brand} {r.car?.model}
                                </div>
                                {r.car?.vinNumber && (
                                    <div className="text-xs text-gray-500">{r.car.vinNumber}</div>
                                )}
                            </td>
                            <td className="px-5 py-4 text-gray-700">{r.reservationDate}</td>
                            <td className="px-5 py-4 text-gray-700">{r.pickupDate}</td>
                            <td className="px-5 py-4">
                                <StatusBadge status={r.status} />
                            </td>

                            <td className="px-5 py-4">
                                <div className="flex flex-wrap justify-end gap-2">
                                    {isStaffOperator && r.status === "PENDING" && (
                                        <button
                                            type="button"
                                            onClick={() => confirm(r.reservationId)}
                                            className="inline-flex items-center gap-1.5 rounded bg-blue-600 px-3 py-2 text-xs font-semibold text-white hover:bg-blue-700"
                                        >
                                            <Check className="h-3.5 w-3.5" />
                                            Confirm
                                        </button>
                                    )}

                                    {isStaffOperator && ["PENDING", "CONFIRMED"].includes(r.status) && (
                                        checkoutReservationId === r.reservationId ? (
                                            <form
                                                onSubmit={(e) => checkout(r.reservationId, e)}
                                                className="grid min-w-52 gap-2 rounded border bg-gray-50 p-2"
                                            >
                                                <input
                                                    type="number"
                                                    min="0"
                                                    value={checkoutForm.startMileage}
                                                    onChange={(e) => updateCheckout("startMileage", e.target.value)}
                                                    placeholder="Start mileage"
                                                    className="rounded border p-2 text-xs"
                                                    required
                                                />
                                                <input
                                                    type="date"
                                                    min={new Date().toISOString().slice(0, 10)}
                                                    value={checkoutForm.returnDate}
                                                    onChange={(e) => updateCheckout("returnDate", e.target.value)}
                                                    className="rounded border p-2 text-xs"
                                                    required
                                                />
                                                <div className="flex gap-2">
                                                    <button
                                                        type="submit"
                                                        className="rounded bg-green-600 px-2 py-1.5 text-xs font-semibold text-white hover:bg-green-700"
                                                    >
                                                        Record
                                                    </button>
                                                    <button
                                                        type="button"
                                                        onClick={() => setCheckoutReservationId(null)}
                                                        className="rounded border border-gray-300 bg-white px-2 py-1.5 text-xs font-semibold text-gray-700 hover:bg-gray-100"
                                                    >
                                                        Close
                                                    </button>
                                                </div>
                                            </form>
                                        ) : (
                                            <button
                                                type="button"
                                                onClick={() => setCheckoutReservationId(r.reservationId)}
                                                className="inline-flex items-center gap-1.5 rounded bg-green-600 px-3 py-2 text-xs font-semibold text-white hover:bg-green-700"
                                            >
                                                <LogOut className="h-3.5 w-3.5" />
                                                Check out
                                            </button>
                                        )
                                    )}

                                    {isCustomer && ["PENDING", "CONFIRMED"].includes(r.status) && (
                                        <button
                                            type="button"
                                            onClick={() => cancel(r.reservationId)}
                                            className="inline-flex items-center gap-1.5 rounded bg-red-600 px-3 py-2 text-xs font-semibold text-white hover:bg-red-700"
                                        >
                                            <X className="h-3.5 w-3.5" />
                                            Cancel
                                        </button>
                                    )}
                                </div>
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            </div>
        )}
        </section>
    );
}
