import { useEffect, useState } from "react";
import { loadStripe } from "@stripe/stripe-js";
import {
    CardCvcElement,
    CardExpiryElement,
    CardNumberElement,
    Elements,
    useElements,
    useStripe,
} from "@stripe/react-stripe-js";
import { paymentService } from "../../services/api";

const cardStyle = { style: { base: { fontSize: "16px" } } };

function CardForm({ clientSecret, paymentIntentId, onPaid }) {
    const stripe = useStripe();
    const elements = useElements();
    const [name, setName] = useState("");
    const [message, setMessage] = useState("");

    const pay = async (e) => {
        e.preventDefault();
        setMessage("");

        if (!stripe || !elements) return setMessage("Stripe is loading.");

        const { error, paymentIntent } = await stripe.confirmCardPayment(clientSecret, {
            payment_method: {
                card: elements.getElement(CardNumberElement),
                billing_details: { name },
            },
        });

        if (error) return setMessage(error.message);

        try {
            await paymentService.confirmCard(paymentIntent?.id || paymentIntentId);
            onPaid?.();
        } catch (error) {
            setMessage(error.message || "Payment failed.");
        }
    };

    return (
        <form onSubmit={pay} className="mt-3 space-y-3">
            <input
                value={name}
                onChange={(e) => setName(e.target.value)}
                placeholder="Name on card"
                className="w-full border p-2"
                required
            />
            <div className="border p-2"><CardNumberElement options={cardStyle} /></div>
            <div className="border p-2"><CardExpiryElement options={cardStyle} /></div>
            <div className="border p-2"><CardCvcElement options={cardStyle} /></div>
            <button className="bg-blue-600 px-3 py-2 text-white">Pay card</button>
            {message && <p className="text-sm">{message}</p>}
        </form>
    );
}

export default function StripeCardPayment({ publishableKey, clientSecret, paymentIntentId, onPaid }) {
    const [stripe, setStripe] = useState(null);
    const [message, setMessage] = useState("");

    useEffect(() => {
        const loadKey = publishableKey
            ? Promise.resolve(publishableKey)
            : paymentService.stripeConfig().then(({ data }) => data.publishableKey);

        loadKey
            .then((key) => {
                if (!key || key.includes("xxx") || key.includes("ReplaceMe")) {
                    setMessage("Stripe key is not configured.");
                    return;
                }
                setStripe(loadStripe(key));
            })
            .catch(() => setMessage("Could not load Stripe."));
    }, [publishableKey]);

    if (message) return <p className="mt-2 text-sm">{message}</p>;
    if (!stripe) return <p className="mt-2 text-sm">Loading card form...</p>;

    return (
        <Elements stripe={stripe}>
            <CardForm clientSecret={clientSecret} paymentIntentId={paymentIntentId} onPaid={onPaid} />
        </Elements>
    );
}
