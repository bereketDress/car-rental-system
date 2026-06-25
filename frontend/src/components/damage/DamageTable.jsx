const statuses = [
    "REPORTED",
    "UNDER_REPAIR",
    "REPAIRED",
    "CLOSED",
];

export default function DamageTable({damages, onStatusChange,}) {
    return (
        <div className="overflow-hidden rounded border border-gray-200 bg-white">
            <div className="overflow-x-auto">
                <table className="min-w-full divide-y divide-gray-200 text-left text-sm">
                    <thead className="bg-gray-50">
                    <tr>
                        <th className="w-20 px-4 py-3 font-semibold text-gray-700">ID</th>
                        <th className="px-4 py-3 font-semibold text-gray-700">Description</th>
                        <th className="w-32 px-4 py-3 font-semibold text-gray-700">Cost</th>
                        <th className="w-48 px-4 py-3 font-semibold text-gray-700">Status</th>
                    </tr>
                    </thead>

                    <tbody className="divide-y divide-gray-100">
                    {damages.length === 0 && (
                        <tr>
                            <td className="px-4 py-6 text-center text-gray-500" colSpan={4}>
                                No damage reports found.
                            </td>
                        </tr>
                    )}

                    {damages.map((damage) => (
                        <tr key={damage.damageId} className="hover:bg-gray-50">
                            <td className="px-4 py-3 text-gray-700">{damage.damageId}</td>

                            <td className="px-4 py-3 font-medium text-gray-900">
                                {damage.description}
                            </td>

                            <td className="px-4 py-3 text-gray-700">
                                ${damage.repairCost}
                            </td>

                            <td className="px-4 py-3">
                                <select
                                    value={damage.status}
                                    onChange={(e) =>
                                        onStatusChange(
                                            damage.damageId,
                                            e.target.value
                                        )
                                    }
                                    className="w-full rounded border border-gray-300 bg-white px-3 py-2 text-sm text-gray-900"
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
            </div>
        </div>
    );
}
