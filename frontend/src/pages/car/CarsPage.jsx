import { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { carService, reservationService } from "../../services/api";
import CarGrid from "../../components/car/CarGrid";
import { useAuth } from "../../context/AuthContext.jsx";

// fetches cars and manages reservation logic.


export default function CarsPage() {
    const [cars, setCars] = useState([]);
    const [carType, setCarType] = useState("");
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState("");
    const { auth, isAuthenticated } = useAuth();
    const navigate = useNavigate();
    const location = useLocation();

    const loadCars = async (type = carType) => {
        setError("");
        setIsLoading(true);

        try {
            const res = await carService.searchAvailable(type);
            setCars(res.data);
        } catch (loadError) {
            setError(loadError.message || "Unable to load available cars.");
        } finally {
            setIsLoading(false);
        }
    };

    useEffect(() => {
        loadCars("");
    }, []);

    const reserveCar = async (car, pickupDate) => {
        if (!isAuthenticated) {
            navigate("/login", { state: { from: location } });
            throw new Error("Sign in before reserving a car.");
        }

        if (auth.role !== "CUSTOMER") {
            throw new Error("Only customers can reserve cars.");
        }

        await reservationService.create({
            vinNumber: car.vinNumber || car.vinNo,
            pickupDate,
        });
        await loadCars();
    };

    const searchCars = (e) => {
        e.preventDefault();
        loadCars(carType);
    };

    return (
        <div className="p-6">
            <div className="mb-6 flex flex-col gap-4 md:flex-row md:items-end md:justify-between">
                <div>
                    <h1 className="text-3xl font-bold">Available Cars</h1>
                    <p className="mt-2 text-sm text-gray-600">
                        Search by car type, choose a pickup date, and reserve an available vehicle.
                    </p>
                </div>

                <form onSubmit={searchCars} className="flex w-full gap-2 md:w-auto">
                    <input
                        value={carType}
                        onChange={(e) => setCarType(e.target.value)}
                        placeholder="Car type"
                        className="min-w-0 flex-1 rounded border p-2 md:w-56"
                    />
                    <button
                        type="submit"
                        className="rounded bg-blue-600 px-4 py-2 font-semibold text-white hover:bg-blue-700"
                    >
                        Search
                    </button>
                </form>
            </div>

            {error && (
                <p className="mb-4 rounded border border-red-200 bg-red-50 p-3 text-sm text-red-700">
                    {error}
                </p>
            )}

            {isLoading ? (
                <div className="rounded border bg-white p-8 text-center text-gray-600">
                    Loading available cars...
                </div>
            ) : (
                <CarGrid
                    cars={cars}
                    onReserve={reserveCar}
                    reserveDisabled={false}
                />
            )}
        </div>
    );
}
