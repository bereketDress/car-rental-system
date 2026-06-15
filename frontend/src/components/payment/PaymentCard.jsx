// Purpose:
// - Display one rental waiting for payment.
// - Allow Cash or Card payment.

import PaymentSection from "../Reservation/PaymentSection";

export default function PaymentCard({
                                        rental,
                                        reload,
                                    }) {
    return (
        <div className="rounded border p-4 shadow">
            <h2 className="font-bold">
                Rental #{rental.rentalId}
            </h2>

            <p>
                Total: ${rental.totalCharge}
            </p>

            <PaymentSection
                rentalId={rental.rentalId}
                amount={rental.totalCharge}
                reload={reload}
            />
        </div>
    );
}
