//displays the admin table with edit/delete actions.

export default function CarTable({cars, onEdit, onRemove,}) {
    return (
        <div className="overflow-hidden rounded border border-gray-200 bg-white">
            <div className="overflow-x-auto">
                <table className="min-w-full divide-y divide-gray-200 text-left text-sm">
                    <thead className="bg-gray-50">
                    <tr>
                        <th className="w-24 px-4 py-3 font-semibold text-gray-700">Car ID</th>
                        <th className="px-4 py-3 font-semibold text-gray-700">Brand</th>
                        <th className="px-4 py-3 font-semibold text-gray-700">Model</th>
                        <th className="w-40 px-4 py-3 font-semibold text-gray-700">Status</th>
                        <th className="w-44 px-4 py-3 font-semibold text-gray-700">Action</th>
                    </tr>
                    </thead>

                    <tbody className="divide-y divide-gray-100">
                    {cars.length === 0 && (
                        <tr>
                            <td className="px-4 py-6 text-center text-gray-500" colSpan={5}>
                                No vehicles found for this branch.
                            </td>
                        </tr>
                    )}

                    {cars.map((car) => (
                        <tr key={car.carId} className="hover:bg-gray-50">
                            <td className="px-4 py-3 text-gray-700">{car.carId}</td>
                            <td className="px-4 py-3 font-medium text-gray-900">{car.brand}</td>

                            <td className="px-4 py-3 text-gray-700">{car.model}</td>

                            <td className="px-4 py-3 text-gray-700">
                                {car.availabilityStatus || car.availability}
                            </td>

                            <td className="px-4 py-3">
                                <div className="flex gap-2">
                                    <button
                                        onClick={() => onEdit?.(car)}
                                        className="rounded border border-gray-300 px-3 py-1 text-gray-900 hover:bg-gray-50"
                                    >
                                        Edit
                                    </button>
                                    <button
                                        onClick={() => onRemove?.(car.carId)}
                                        className="rounded bg-red-600 px-3 py-1 text-white hover:bg-red-700"
                                    >
                                        Remove
                                    </button>
                                </div>
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            </div>
        </div>
    );
}
