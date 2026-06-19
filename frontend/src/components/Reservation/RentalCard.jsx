import { useEffect, useState } from "react";
import PaymentSection from "./PaymentSection";
import { rentalService } from "../../services/rentalService";

const emptyForm = {
    endMileage: "",
    damageDescription: "",
    repairCost: "",
    paymentMethod: "CASH",
};

export default function RentalCard({ rental, isPaid = false, reload, role }) {
    const [currentRental, setCurrentRental] = useState(rental);
    const [form, setForm] = useState(emptyForm);
    const [showForm, setShowForm] = useState(false);
    const [cardPayment, setCardPayment] = useState(null);
    const [paid, setPaid] = useState(false);
    const [message, setMessage] = useState("");

    const canCheckIn = ["STAFF", "MANAGER"].includes(role) && currentRental.status === "ACTIVE";
    const canPay = currentRental.status === "RETURNED" && !isPaid && !paid;

    useEffect(() => {
        setCurrentRental(rental);
        setForm(emptyForm);
        setShowForm(false);
        setCardPayment(null);
        setPaid(false);
        setMessage("");
    }, [rental]);

    const update = (field, value) => setForm((old) => ({ ...old, [field]: value }));

    const checkIn = async (e) => {
        e.preventDefault();
        setMessage("");

        try {
            const { data } = await rentalService.checkin(currentRental.rentalId, {
                ...form,
                repairCost: form.damageDescription ? form.repairCost || "0" : "",
            });

            setCurrentRental(data.rental);
            setCardPayment(data.requiresCardPayment ? data : null);
            setPaid(!data.requiresCardPayment);
            setShowForm(false);
            setMessage(data.requiresCardPayment ? "Complete card payment." : "Cash payment completed.");
            if (!data.requiresCardPayment) setTimeout(() => reload?.(), 800);
        } catch (error) {
            setMessage(error.message || "Check-in failed.");
        }
    };

    return (
        <div className="border p-4">
            <h3 className="font-bold">{currentRental.car?.brand} {currentRental.car?.model}</h3>
            <p>Rental #{currentRental.rentalId}</p>
            <p>Status: {currentRental.status}</p>
            <p>Total: ${currentRental.totalCharge}</p>
            <p>Damage: ${currentRental.damageRepairCost ?? 0}</p>
            {currentRental.startMileage && <p>Start mileage: {currentRental.startMileage}</p>}

            {canCheckIn && !showForm && (
                <button onClick={() => setShowForm(true)} className="mt-3 bg-blue-600 px-3 py-2 text-white">
                    Check in
                </button>
            )}

            {canCheckIn && showForm && (
                <form onSubmit={checkIn} className="mt-3 space-y-3">
                    <input
                        type="number"
                        min={currentRental.startMileage || 0}
                        value={form.endMileage}
                        onChange={(e) => update("endMileage", e.target.value)}
                        placeholder="Return mileage"
                        className="w-full border p-2"
                        required
                    />

                    <textarea
                        value={form.damageDescription}
                        onChange={(e) => update("damageDescription", e.target.value)}
                        placeholder="Damage description"
                        className="w-full border p-2"
                    />

                    {form.damageDescription && (
                        <input
                            type="number"
                            min="0"
                            step="0.01"
                            value={form.repairCost}
                            onChange={(e) => update("repairCost", e.target.value)}
                            placeholder="Repair cost"
                            className="w-full border p-2"
                        />
                    )}

                    <select
                        value={form.paymentMethod}
                        onChange={(e) => update("paymentMethod", e.target.value)}
                        className="w-full border p-2"
                    >
                        <option value="CASH">Cash</option>
                        <option value="CARD">Card</option>
                    </select>

                    <button className="bg-blue-600 px-3 py-2 text-white">Save check-in</button>
                    <button type="button" onClick={() => setShowForm(false)} className="ml-2 border px-3 py-2">Cancel</button>
                </form>
            )}

            {canPay && (
                <PaymentSection
                    rentalId={currentRental.rentalId}
                    reload={reload}
                    role={role}
                    defaultOpen={!!cardPayment}
                    cardPayment={cardPayment}
                />
            )}

            {message && <p className="mt-2 text-sm">{message}</p>}
        </div>
    );
}
