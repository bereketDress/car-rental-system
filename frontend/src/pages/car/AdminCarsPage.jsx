import { useEffect, useState } from "react";
import { branchService } from "../../services/branchService";
import { managerService } from "../../services/managerService";
import CarForm from "../../components/car/CarForm";
import CarTable from "../../components/car/CarTable";


// coordinates admin operations and state.

export default function AdminCarsPage() {
    const [cars, setCars] = useState([]);
    const [branches, setBranches] = useState([]);
    const [branchId, setBranchId] = useState("");
    const [editingCar, setEditingCar] = useState(null);
    const [message, setMessage] = useState("");

    const loadBranches = async () => {
        const res = await branchService.listAll();
        setBranches(res.data);

        if (!branchId && res.data[0]?.branchId) {
            setBranchId(String(res.data[0].branchId));
        }
    };

    const loadCars = async (nextBranchId = branchId) => {
        if (!nextBranchId) {
            setCars([]);
            return;
        }

        const res = await managerService.branchVehicles(nextBranchId);
        setCars(res.data);
    };

    useEffect(() => {
        loadBranches();
    }, []);

    useEffect(() => {
        loadCars(branchId);
    }, [branchId]);

    const saveVehicle = async (car) => {
        setMessage("");

        try {
            if (editingCar) {
                await managerService.updateVehicle(editingCar.carId, car);
                setMessage("Vehicle updated.");
            } else {
                await managerService.addVehicle(branchId, car);
                setMessage("Vehicle added.");
            }

            setEditingCar(null);
            await loadCars();
        } catch (error) {
            setMessage(error.message || "Vehicle save failed.");
        }
    };

    const removeVehicle = async (carId) => {
        setMessage("");

        try {
            await managerService.removeVehicle(branchId, carId);
            setMessage("Vehicle removed from branch inventory.");
            await loadCars();
        } catch (error) {
            setMessage(error.message || "Vehicle removal failed.");
        }
    };

    return (
        <div className="mx-auto max-w-6xl p-6">
            <h1 className="mb-6 text-3xl font-bold text-gray-900">
                Manage Vehicles
            </h1>

            <div className="mb-4 max-w-sm">
                <label className="grid gap-1">
                    <span className="text-sm font-medium text-gray-700">Branch inventory</span>
                    <select
                        value={branchId}
                        onChange={(e) => {
                            setBranchId(e.target.value);
                            setEditingCar(null);
                        }}
                        className="rounded border p-2"
                    >
                        {branches.map((branch) => (
                            <option key={branch.branchId} value={branch.branchId}>
                                {branch.name}
                            </option>
                        ))}
                    </select>
                </label>
            </div>

            {message && (
                <p className="mb-4 rounded bg-gray-50 p-3 text-gray-700">{message}</p>
            )}

            <CarForm
                onSave={saveVehicle}
                editingCar={editingCar}
                onCancel={() => setEditingCar(null)}
            />

            <CarTable
                cars={cars}
                onEdit={setEditingCar}
                onRemove={removeVehicle}
            />
        </div>
    );
}
