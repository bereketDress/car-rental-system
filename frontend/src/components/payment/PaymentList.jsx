// Purpose:
// - Loop through rentals.
// - Render one PaymentCard for each unpaid rental.

import PaymentCard from "./PaymentCard";

function paymentRentalId(payment) {
    return payment.rentalId ?? payment.rental?.rentalId;
}

export default function PaymentList({
                                        rentals,
                                        payments = [],
                                        reload,
                                    }) {
    const paidRentalIds = new Set(
        payments
            .filter((payment) => payment.status === "COMPLETED")
            .map(paymentRentalId)
            .filter(Boolean)
    );

    const payableRentals = rentals.filter((rental) =>
        rental.status === "RETURNED" && !paidRentalIds.has(rental.rentalId)
    );

    if (!payableRentals.length) {
        return (
            <div className="mb-8 rounded border bg-gray-50 p-4 text-gray-700">
                No rentals are ready for payment yet. After staff checks in a rental, the Pay button will appear here.
            </div>
        );
    }

    return (
        <div className="grid gap-4 mb-8">
            {payableRentals.map((rental) => (
                <PaymentCard
                    key={rental.rentalId}
                    rental={rental}
                    reload={reload}
                />
            ))}
        </div>
    );
}
