
// - Load reservations and rentals.
// - Handle API calls.
// - Pass data to child components.

import { CalendarCheck, CreditCard, Gauge, RefreshCw } from "lucide-react";
import { useEffect, useMemo, useState } from "react";
import { paymentService } from "../../services/paymentService";
import { rentalService } from "../../services/rentalService";
import { reservationService } from "../../services/reservationService";
import ReservationTable from "../../components/Reservation/ReservationTable";
import RentalList from "../../components/Reservation/RentalList";
import { useAuth } from "../../context/AuthContext.jsx";

export default function ReservationsPage() {
    const [reservations, setReservations] = useState([]);
    const [rentals, setRentals] = useState([]);
    const [payments, setPayments] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState("");
    const { auth } = useAuth();
    const role = auth?.role;
    const isCustomer = role === "CUSTOMER";
    const isStaffOperator = role === "STAFF" || role === "MANAGER";
    const title = role === "CUSTOMER" ? "My Reservations" : "Reservation Operations";

    const loadData = async () => {
        setError("");
        setIsLoading(true);

        try {
            const [res1, res2, res3] = await Promise.all([
                reservationService.list(),
                rentalService.list(),
                paymentService.listAll(),
            ]);

            setReservations(res1.data);
            setRentals(res2.data);
            setPayments(res3.data);
        } catch (loadError) {
            setError(loadError.message || "Unable to load reservations.");
        } finally {
            setIsLoading(false);
        }
    };

    useEffect(() => {
        loadData();
    }, []);

    const paidRentalIds = useMemo(
        () => new Set(
            payments
                .filter((payment) => payment.status === "COMPLETED")
                .map((payment) => payment.rentalId)
        ),
        [payments]
    );

    const summary = useMemo(() => {
        const pendingReservations = reservations.filter((item) => item.status === "PENDING").length;
        const confirmedReservations = reservations.filter((item) => item.status === "CONFIRMED").length;
        const activeRentals = rentals.filter((item) => item.status === "ACTIVE").length;
        const awaitingPayment = rentals.filter(
            (item) => item.status === "RETURNED" && !paidRentalIds.has(item.rentalId)
        ).length;

        if (isCustomer) {
            return [
                {
                    label: "Upcoming",
                    value: pendingReservations + confirmedReservations,
                    icon: CalendarCheck,
                },
                {
                    label: "Active rentals",
                    value: activeRentals,
                    icon: Gauge,
                },
                {
                    label: "Awaiting payment",
                    value: awaitingPayment,
                    icon: CreditCard,
                },
            ];
        }

        return [
            {
                label: "Pending approvals",
                value: pendingReservations,
                icon: CalendarCheck,
            },
            {
                label: "Active rentals",
                value: activeRentals,
                icon: Gauge,
            },
            {
                label: "Payment due",
                value: awaitingPayment,
                icon: CreditCard,
            },
        ];
    }, [isCustomer, paidRentalIds, rentals, reservations]);

    return (
        <div className="min-h-screen bg-gray-50 p-6">
            <div className="mx-auto max-w-7xl">
                <div className="mb-6 flex flex-col gap-4 md:flex-row md:items-end md:justify-between">
                    <div>
                        <p className="text-sm font-medium uppercase tracking-wide text-blue-700">
                            Reservations
                        </p>
                        <h1 className="mt-1 text-3xl font-bold text-gray-950">
                            {title}
                        </h1>
                        <p className="mt-2 max-w-2xl text-sm text-gray-600">
                            {isStaffOperator
                                ? "Confirm bookings, check out vehicles, and collect outstanding rental payments."
                                : "Track upcoming bookings, active rentals, and balances that are ready for payment."}
                        </p>
                    </div>

                    <button
                        type="button"
                        onClick={loadData}
                        disabled={isLoading}
                        className="inline-flex items-center justify-center gap-2 rounded border border-gray-300 bg-white px-4 py-2 text-sm font-medium text-gray-800 shadow-sm hover:bg-gray-100 disabled:cursor-not-allowed disabled:opacity-60"
                    >
                        <RefreshCw className={`h-4 w-4 ${isLoading ? "animate-spin" : ""}`} />
                        Refresh
                    </button>
                </div>

                {error && (
                    <p className="mb-4 rounded border border-red-200 bg-red-50 p-3 text-sm text-red-700">
                        {error}
                    </p>
                )}

                <div className="mb-6 grid gap-4 md:grid-cols-3">
                    {summary.map((item) => {
                        const Icon = item.icon;

                        return (
                            <div key={item.label} className="rounded-lg border bg-white p-5 shadow-sm">
                                <div className="flex items-center justify-between">
                                    <div>
                                        <p className="text-sm font-medium text-gray-500">{item.label}</p>
                                        <p className="mt-2 text-3xl font-bold text-gray-950">{item.value}</p>
                                    </div>

                                    <span className="rounded bg-blue-50 p-3 text-blue-700">
                                        <Icon className="h-5 w-5" />
                                    </span>
                                </div>
                            </div>
                        );
                    })}
                </div>

                {isLoading ? (
                    <div className="rounded-lg border bg-white p-8 text-center text-gray-600 shadow-sm">
                        Loading reservation activity...
                    </div>
                ) : (
                    <div className="grid gap-6">
                        <ReservationTable
                            reservations={reservations}
                            reload={loadData}
                            role={role}
                        />

                        <RentalList
                            rentals={rentals}
                            payments={payments}
                            reload={loadData}
                            role={role}
                        />
                    </div>
                )}
            </div>
        </div>
    );
}
