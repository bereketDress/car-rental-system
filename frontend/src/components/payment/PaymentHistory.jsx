// Purpose:
// - Display all completed and pending payments.

export default function PaymentHistory({
                                           payments,
                                       }) {
    return (
        <div className="overflow-hidden rounded border border-gray-200 bg-white">
            <div className="overflow-x-auto">
                <table className="min-w-full divide-y divide-gray-200 text-left text-sm">
                    <thead className="bg-gray-50">
                    <tr>
                        <th className="w-20 px-4 py-3 font-semibold text-gray-700">ID</th>
                        <th className="w-28 px-4 py-3 font-semibold text-gray-700">Rental</th>
                        <th className="px-4 py-3 font-semibold text-gray-700">Method</th>
                        <th className="w-32 px-4 py-3 font-semibold text-gray-700">Amount</th>
                        <th className="w-40 px-4 py-3 font-semibold text-gray-700">Status</th>
                    </tr>
                    </thead>

                    <tbody className="divide-y divide-gray-100">
                    {payments.length === 0 && (
                        <tr>
                            <td className="px-4 py-6 text-center text-gray-500" colSpan={5}>
                                No payments found.
                            </td>
                        </tr>
                    )}

                    {payments.map((payment) => {
                        const rentalId = payment.rentalId ?? payment.rental?.rentalId;

                        return (
                            <tr key={payment.paymentId} className="hover:bg-gray-50">
                                <td className="px-4 py-3 text-gray-700">{payment.paymentId}</td>
                                <td className="px-4 py-3 text-gray-700">{rentalId}</td>
                                <td className="px-4 py-3 font-medium text-gray-900">
                                    {payment.paymentMethod}
                                </td>
                                <td className="px-4 py-3 text-gray-700">${payment.amount}</td>
                                <td className="px-4 py-3 text-gray-700">{payment.status}</td>
                            </tr>
                        );
                    })}
                    </tbody>
                </table>
            </div>
        </div>
    );
}
