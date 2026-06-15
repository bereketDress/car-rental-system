import CarCard from "./CarCard";

// renders the list of cars.

export default function CarGrid({ cars, onReserve, reserveDisabled }) {
    if (!cars.length) {
        return (
            <div className="rounded border bg-white p-8 text-center text-gray-600">
                No available cars found.
            </div>
        );
    }

    return (
        <div className="grid gap-4 md:grid-cols-3">
            {cars.map((car) => (
                <CarCard
                    key={car.vinNumber || car.vinNo}
                    car={car}
                    onReserve={onReserve}
                    disabled={reserveDisabled}
                />
            ))}
        </div>
    );
}
