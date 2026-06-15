import { useState } from "react";
import StripeCardPayment from "../cardPayment/StripeCardPayment";
import { paymentService } from "../../services/api";

const isStaff = (role) => role === "STAFF" || role === "MANAGER";

export default function PaymentSection({ rentalId, reload, defaultOpen = false, role = "", cardPayment = null }) {
    const [open, setOpen] = useState(defaultOpen || !!cardPayment);
    const [method, setMethod] = useState(cardPayment ? "CARD" : "");
    const [cardData, setCardData] = useState(cardPayment);
    const [message, setMessage] = useState("");
    const [loading, setLoading] = useState(false);

    const done = (text) => {
        setMessage(text);
        setTimeout(() => reload?.(), 800);
    };

    const payCash = async () => {
        setLoading(true);
        setMessage("");
        try {
            const pay = isStaff(role) ? paymentService.process : paymentService.record;
            await pay({ rentalId, paymentMethod: "CASH" });
            done("Cash payment completed.");
        } catch (error) {
            setMessage(error.message || "Cash payment failed.");
        } finally {
            setLoading(false);
        }
    };

    const payCard = async () => {
        setMethod("CARD");
        if (cardData) return;

        try {
            const { data } = await paymentService.createIntent(rentalId);
            setCardData(data);
        } catch (error) {
            setMessage(error.message || "Card payment failed.");
        }
    };

    if (!open) {
        return <button onClick={() => setOpen(true)} className="mt-3 bg-blue-600 px-3 py-2 text-white">Pay</button>;
    }

    return (
        <div className="mt-3 space-y-3">
            <div className="flex gap-2">
                <button onClick={() => setMethod("CASH")} className="border px-3 py-2">Cash</button>
                <button onClick={payCard} className="border px-3 py-2">Card</button>
            </div>

            {method === "CASH" && (
                <button disabled={loading} onClick={payCash} className="bg-green-600 px-3 py-2 text-white disabled:bg-gray-400">
                    {loading ? "Processing..." : "Pay cash"}
                </button>
            )}

            {method === "CARD" && cardData && (
                <StripeCardPayment {...cardData} onPaid={() => done("Card payment completed.")} />
            )}

            {message && <p className="text-sm">{message}</p>}
        </div>
    );
}
