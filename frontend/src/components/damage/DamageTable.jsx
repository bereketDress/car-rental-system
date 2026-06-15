const statuses = [
    "REPORTED",
    "UNDER_REPAIR",
    "REPAIRED",
    "CLOSED",
];

export default function DamageTable({damages, onStatusChange,}) {
    return (
        <table className="w-full border">
            <thead>
            <tr>
                <th>ID</th>
                <th>Description</th>
                <th>Cost</th>
                <th>Status</th>
            </tr>
            </thead>

            <tbody>
            {damages.map((damage) => (
                <tr key={damage.damageId}>
                    <td>{damage.damageId}</td>

                    <td>{damage.description}</td>

                    <td>${damage.repairCost}</td>

                    <td>
                        <select
                            value={damage.status}
                            onChange={(e) =>
                                onStatusChange(
                                    damage.damageId,
                                    e.target.value
                                )
                            }
                        >
                            {statuses.map((status) => (
                                <option
                                    key={status}
                                    value={status}
                                >
                                    {status}
                                </option>
                            ))}
                        </select>
                    </td>
                </tr>
            ))}
            </tbody>
        </table>
    );
}