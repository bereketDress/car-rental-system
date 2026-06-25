import { useEffect, useState } from "react";

    //add/edit car form.

const emptyCar = {
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

    const inputClass = "w-full rounded border border-gray-300 px-3 py-2 text-gray-900 outline-none focus:border-blue-500 focus:ring-1 focus:ring-blue-500";
    const labelClass = "grid gap-1 text-sm font-medium text-gray-700";

    return (
        <form onSubmit={save} className="mb-8 rounded border border-gray-200 bg-white p-4">
            <div className="mb-4 flex items-center justify-between gap-3">
                <h2 className="text-lg font-semibold text-gray-900">
                    {editingCar ? "Edit Vehicle" : "Add Vehicle"}
                </h2>
            </div>

            <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
                <label className={labelClass}>
                    <span>Plate number</span>
                    <input
                        value={form.plateNumber}
                        onChange={(e) => updateField("plateNumber", e.target.value)}
                        placeholder="AA-10001"
                        className={inputClass}
                    />
                </label>

                <label className={labelClass}>
                    <span>Brand</span>
                    <input
                        value={form.brand}
                        onChange={(e) => updateField("brand", e.target.value)}
                        placeholder="Toyota"
                        className={inputClass}
                        required
                    />
                </label>

                <label className={labelClass}>
                    <span>Model</span>
                    <input
                        value={form.model}
                        onChange={(e) => updateField("model", e.target.value)}
                        placeholder="Corolla"
                        className={inputClass}
                        required
                    />
                </label>

                <label className={labelClass}>
                    <span>Year</span>
                    <input
                        value={form.year}
                        onChange={(e) => updateField("year", e.target.value)}
                        type="number"
                        placeholder="2026"
                        className={inputClass}
                        required
                    />
                </label>

                <label className={labelClass}>
                    <span>Mileage</span>
                    <input
                        value={form.mileage}
                        onChange={(e) => updateField("mileage", e.target.value)}
                        type="number"
                        min="0"
                        step="0.1"
                        placeholder="0"
                        className={inputClass}
                        required
                    />
                </label>

                <label className={labelClass}>
                    <span>Daily rate</span>
                    <input
                        value={form.dailyRate}
                        onChange={(e) => updateField("dailyRate", e.target.value)}
                        type="number"
                        min="0"
                        step="0.01"
                        placeholder="0.00"
                        className={inputClass}
                        required
                    />
                </label>

                <label className={labelClass}>
                    <span>Car type</span>
                    <input
                        value={form.carType}
                        onChange={(e) => updateField("carType", e.target.value)}
                        placeholder="Sedan"
                        className={inputClass}
                    />
                </label>

                <label className={labelClass}>
                    <span>Status</span>
                    <select
                        value={form.availability}
                        onChange={(e) => updateField("availability", e.target.value)}
                        className={inputClass}
                    >
                        <option value="AVAILABLE">Available</option>
                        <option value="UNAVAILABLE">Unavailable</option>
                    </select>
                </label>
            </div>

            <div className="mt-4 flex gap-2">
                <button className="rounded bg-blue-600 px-4 py-2 font-medium text-white hover:bg-blue-700">
                    {editingCar ? "Update Vehicle" : "Add Vehicle"}
                </button>

                {editingCar && (
                    <button
                        type="button"
                        onClick={onCancel}
                        className="rounded border border-gray-300 px-4 py-2 font-medium text-gray-900 hover:bg-gray-50"
                    >
                        Cancel
                    </button>
                )}
            </div>
        </form>
    );
}
