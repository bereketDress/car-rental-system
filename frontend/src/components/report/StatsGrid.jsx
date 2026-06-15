
// - Display all report statistics.
// - Render one StatsCard for each metric.

import StatsCard from "./StatsCard";

export default function StatsGrid({ stats }) {
    return (
        <div className="grid gap-4 md:grid-cols-3">
            <StatsCard title="Fleet Size" value={stats.totalFleetSize} />
            <StatsCard title="Available Cars" value={stats.availableCars} />
            <StatsCard title="Rented Cars" value={stats.rentedCars} />
            <StatsCard title="Total Rentals" value={stats.totalRentals} />
            <StatsCard title="Active Rentals" value={stats.activeRentals} />
            <StatsCard title="Returned Rentals" value={stats.completedRentals} />
            <StatsCard title="Customers" value={stats.totalCustomers} />
            <StatsCard title="Revenue" value={`$${Number(stats.totalRevenue || 0).toFixed(2)}`} />
        </div>
    );
}
