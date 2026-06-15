import { useEffect, useState } from "react";

    //add/edit car form.

const emptyCar = {
    vinNumber: "",
    plateNumber: "",
    brand: "",
    model: "",
    year: new Date().getFullYear(),
    mileage: 0,
    availability: "AVAILABLE",
    dailyRate: 0,
    carType: "",
};

function toForm(car) {
    if (!car) return emptyCar;

    return {
        vinNumber: car.vinNumber || car.vinNo || "",
        plateNumber: car.plateNumber || "",
        brand: car.brand || "",
        model: car.model || "",
        year: car.year || new Date().getFullYear(),
        mileage: car.mileage || 0,
        availability: car.availabilityStatus || car.availability || "AVAILABLE",
        dailyRate: car.dailyRate || 0,
        carType: car.carType || "",
    };
}

export default function CarForm({ onSave, editingCar, onCancel }) {
    const [form, setForm] = useState(emptyCar);

    useEffect(() => {
        setForm(toForm(editingCar));
    }, [editingCar]);

    const updateField = (field, value) => {
        setForm((current) => ({ ...current, [field]: value }));
    };

    const save = async (e) => {
        e.preventDefault();

        await onSave?.({
            ...form,
            year: Number(form.year),
            mileage: Number(form.mileage),
            dailyRate: Number(form.dailyRate),
        });

        setForm(emptyCar);
    };

    return (
        <form onSubmit={save} className="mb-6 grid gap-2 md:grid-cols-4">
            <input
                value={form.vinNumber}
                onChange={(e) => updateField("vinNumber", e.target.value)}
                placeholder="VIN"
                className="border p-2"
                required
            />

            <input
                value={form.plateNumber}
                onChange={(e) => updateField("plateNumber", e.target.value)}
                placeholder="Plate number"
                className="border p-2"
            />

            <input
                value={form.brand}
                onChange={(e) => updateField("brand", e.target.value)}
                placeholder="Brand"
                className="border p-2"
                required
            />

            <input
                value={form.model}
                onChange={(e) => updateField("model", e.target.value)}
                placeholder="Model"
                className="border p-2"
                required
            />

            <input
                value={form.year}
                onChange={(e) => updateField("year", e.target.value)}
                type="number"
                placeholder="Year"
                className="border p-2"
                required
            />

            <input
                value={form.mileage}
                onChange={(e) => updateField("mileage", e.target.value)}
                type="number"
                min="0"
                step="0.1"
                placeholder="Mileage"
                className="border p-2"
                required
            />

            <input
                value={form.dailyRate}
                onChange={(e) => updateField("dailyRate", e.target.value)}
                type="number"
                min="0"
                step="0.01"
                placeholder="Daily rate"
                className="border p-2"
                required
            />

            <input
                value={form.carType}
                onChange={(e) => updateField("carType", e.target.value)}
                placeholder="Car type"
                className="border p-2"
            />

            <select
                value={form.availability}
                onChange={(e) => updateField("availability", e.target.value)}
                className="border p-2"
            >
                <option value="AVAILABLE">Available</option>
                <option value="UNAVAILABLE">Unavailable</option>
            </select>

            <div className="flex gap-2 md:col-span-4">
                <button className="rounded bg-blue-600 px-4 py-2 text-white">
                    {editingCar ? "Update Vehicle" : "Add Vehicle"}
                </button>

                {editingCar && (
                    <button
                        type="button"
                        onClick={onCancel}
                        className="rounded border px-4 py-2"
                    >
                        Cancel
                    </button>
                )}
            </div>
        </form>
    );
}
