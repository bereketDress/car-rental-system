
import { useState } from "react";

// displays a single car and its reserve button.

function nextDate() {
    const date = new Date();
    date.setDate(date.getDate() + 1);
    return date.toISOString().slice(0, 10);
}

export default function CarCard({ car, onReserve, disabled }) {
    const [pickupDate, setPickupDate] = useState(nextDate());
    const [message, setMessage] = useState("");
    const isReserved = !car.availability;
    const vinNumber = car.vinNumber || car.vinNo;

    const reserve = async () => {
        if (isReserved) return;
        setMessage("");

        try {
            await onReserve?.(car, pickupDate);
            setMessage("Reservation created.");
        } catch (error) {
            setMessage(error.message || "Reservation failed.");
        }
    };

    return (
        <div className="rounded-lg border p-4 shadow">
            <h2 className="text-xl font-bold">
                {car.brand} {car.model}
            </h2>

            <p>{car.carType}</p>
            {vinNumber && (
                <p className="text-xs text-gray-500">{vinNumber}</p>
            )}

            <p className="font-semibold">
                ${car.dailyRate}/day
            </p>

            {isReserved ? (
                <p className="mt-3 text-sm font-semibold text-red-600">Reserved</p>
            ) : (
                <input
                    type="date"
                    value={pickupDate}
                    min={new Date().toISOString().slice(0, 10)}
                    onChange={(e) => setPickupDate(e.target.value)}
                    className="mt-3 w-full rounded border p-2"
                />
            )}

            <button
                type="button"
                disabled={disabled || isReserved}
                onClick={reserve}
                className="mt-3 rounded bg-blue-600 px-4 py-2 text-white disabled:cursor-not-allowed disabled:bg-gray-400"
            >
                {isReserved ? "Reserved" : "Reserve"}
            </button>

            {message && (
                <p className="mt-2 text-sm text-gray-700">{message}</p>
            )}
        </div>
    );
}
