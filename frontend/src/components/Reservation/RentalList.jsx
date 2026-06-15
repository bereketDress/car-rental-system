
// - Render all rentals.

import RentalCard from "./RentalCard";

function paymentRentalId(payment) {
    return payment.rentalId ?? payment.rental?.rentalId;
}

export default function RentalList({
                                       rentals,
                                       payments = [],
                                       reload,
                                       role,
                                   }) {
    const paidRentalIds = new Set(
        payments
            .filter((payment) => payment.status === "COMPLETED")
            .map(paymentRentalId)
            .filter(Boolean)
    );
    const isStaffOperator = role === "STAFF" || role === "MANAGER";

    const visibleRentals = rentals.filter((rental) => {
        const isPaid = paidRentalIds.has(rental.rentalId);

        if (isStaffOperator) {
            return rental.status === "ACTIVE" || (rental.status === "RETURNED" && !isPaid);
        }

        if (role === "CUSTOMER") {
            return rental.status === "RETURNED" && !isPaid;
        }

        return false;
    });

    if (!visibleRentals.length) {
        return (
            <section className="rounded-lg border bg-white p-8 text-center text-gray-600 shadow-sm">
                No rentals are waiting for check-in or payment.
            </section>
        );
    }

    return (
        <section className="rounded-lg border bg-white shadow-sm">
            <div className="border-b p-5">
                <h2 className="text-lg font-bold text-gray-950">Rental Follow-up</h2>
                <p className="text-sm text-gray-600">Vehicles that need check-in or payment collection.</p>
            </div>

            <div className="grid gap-4 p-5 md:grid-cols-2">
                {visibleRentals.map((rental) => (
                    <RentalCard
                        key={rental.rentalId}
                        rental={rental}
                        isPaid={paidRentalIds.has(rental.rentalId)}
                        reload={reload}
                        role={role}
                    />
                ))}
            </div>
        </section>
    );
}
