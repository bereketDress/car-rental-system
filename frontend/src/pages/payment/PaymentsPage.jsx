// Purpose:
// - Load rentals and payments from the backend.
// - Handle payment logic.
// - Pass data to child components.

import { useEffect, useState } from "react";
import { paymentService } from "../../services/paymentService";
import { rentalService } from "../../services/rentalService";
import PaymentList from "../../components/payment/PaymentList";
import PaymentHistory from "../../components/payment/PaymentHistory";
import { useAuth } from "../../context/AuthContext.jsx";

export default function PaymentsPage() {
    const [rentals, setRentals] = useState([]);
    const [payments, setPayments] = useState([]);
    const { auth } = useAuth();
    const isCustomer = auth?.role === "CUSTOMER";
    const title = isCustomer ? "My Payments" : "Payment History";

    const loadData = async () => {
        const [r, p] = await Promise.all([
            rentalService.list(),
            paymentService.listAll(),
        ]);

        setRentals(r.data);
        setPayments(p.data);
    };

    useEffect(() => {
        loadData();
    }, []);

    return (
        <div className="mx-auto max-w-6xl p-6">
            <h1 className="mb-6 text-3xl font-bold text-gray-900">
                {title}
            </h1>

            {isCustomer && (
                <PaymentList
                    rentals={rentals}
                    payments={payments}
                    reload={loadData}
                />
            )}

            <PaymentHistory
                payments={payments}
            />
        </div>
    );
}
