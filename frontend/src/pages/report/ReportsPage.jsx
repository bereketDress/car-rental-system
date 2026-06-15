// Purpose:
// - Load manager operational reports.
// - Optionally filter by branch.
// - Pass statistics to StatsGrid.

import { useEffect, useState } from "react";
import { branchService, managerService } from "../../services/api";
import StatsGrid from "../../components/report/StatsGrid";

export default function ReportsPage() {
    const [branches, setBranches] = useState([]);
    const [branchId, setBranchId] = useState("");
    const [stats, setStats] = useState(null);
    const [error, setError] = useState("");

    useEffect(() => {
        branchService.listAll()
            .then((res) => setBranches(res.data))
            .catch(() => setBranches([]));
    }, []);

    useEffect(() => {
        setError("");
        managerService.reports(branchId)
            .then((res) => setStats(res.data))
            .catch((loadError) => setError(loadError.message || "Unable to load reports."));
    }, [branchId]);

    return (
        <div className="p-6">
            <div className="mb-6 flex flex-col gap-4 md:flex-row md:items-end md:justify-between">
                <div>
                    <h1 className="text-3xl font-bold">
                        Operational Reports
                    </h1>
                    <p className="mt-2 text-sm text-gray-600">
                        Fleet availability, rental activity, customer count, and revenue.
                    </p>
                </div>

                <label className="grid gap-1">
                    <span className="text-sm font-medium text-gray-700">Branch</span>
                    <select
                        value={branchId}
                        onChange={(e) => setBranchId(e.target.value)}
                        className="rounded border p-2"
                    >
                        <option value="">All branches</option>
                        {branches.map((branch) => (
                            <option key={branch.branchId} value={branch.branchId}>
                                {branch.name}
                            </option>
                        ))}
                    </select>
                </label>
            </div>

            {error && (
                <p className="mb-4 rounded border border-red-200 bg-red-50 p-3 text-sm text-red-700">
                    {error}
                </p>
            )}

            {stats ? (
                <StatsGrid stats={stats} />
            ) : (
                <div className="rounded border bg-white p-8 text-center text-gray-600">
                    Loading reports...
                </div>
            )}
        </div>
    );
}
