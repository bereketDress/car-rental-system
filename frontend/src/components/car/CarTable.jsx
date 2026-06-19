//displays the admin table with edit/delete actions.

export default function CarTable({cars, onEdit, onRemove,}) {
    return (
        <table className="w-full border">
            <thead>
            <tr>
                <th>Car ID</th>
                <th>Brand</th>
                <th>Model</th>
                <th>Status</th>
                <th>Action</th>
            </tr>
            </thead>

            <tbody>
            {cars.map((car) => (
                <tr key={car.carId}>
                    <td>{car.carId}</td>
                    <td>{car.brand}</td>

                    <td>{car.model}</td>

                    <td>{car.availabilityStatus || car.availability}</td>

                    <td>
                        <button
                            onClick={() => onEdit?.(car)}
                            className="mr-3 text-blue-600"
                        >
                            Edit
                        </button>
                        <button
                            onClick={() => onRemove?.(car.carId)}
                            className="text-red-600"
                        >
                            Remove
                        </button>
                    </td>
                </tr>
            ))}
            </tbody>
        </table>
    );
}
