
// - Display a single report statistic.

export default function StatsCard({ title, value }) {
    return (
        <div className="rounded-lg border bg-white p-4 shadow">
            <p className="text-sm text-gray-500">{title}</p>
            <h2 className="mt-2 text-2xl font-bold">
                {value}
            </h2>
        </div>
    );
}