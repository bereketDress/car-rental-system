// Purpose:
// - Display all completed and pending payments.

export default function PaymentHistory({
                                           payments,
                                       }) {
    return (
        <table className="w-full border">
            <thead>
            <tr>
                <th>ID</th>
                <th>Rental</th>
                <th>Method</th>
                <th>Amount</th>
                <th>Status</th>
            </tr>
            </thead>

            <tbody>
            {payments.map((payment) => {
                const rentalId = payment.rentalId ?? payment.rental?.rentalId;

                return (
                <tr key={payment.paymentId}>
                    <td>{payment.paymentId}</td>
                    <td>{rentalId}</td>
                    <td>{payment.paymentMethod}</td>
                    <td>${payment.amount}</td>
                    <td>{payment.status}</td>
                </tr>
                );
            })}
            </tbody>
        </table>
    );
}
