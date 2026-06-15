
// - Landing page of the application.
// - Introduces the Car Rental Management System.
// - Highlights main features.

export default function HomePage() {
    const features = [
        {
            title: "Wide Selection",
            text: "Choose from economy to luxury cars.",
        },
        {
            title: "Easy Booking",
            text: "Reserve your car in just a few clicks.",
        },
        {
            title: "Reliable Service",
            text: "Well-maintained vehicles for safe travel.",
        },
    ];

    return (
        <div className="min-h-screen bg-gray-50 p-8 text-center">
            <h1 className="mb-3 text-4xl font-bold">
                Car Rental Management System
            </h1>

            <p className="mb-10 text-gray-600">
                Find and reserve your perfect car.
            </p>

            <div className="grid gap-6 md:grid-cols-3">
                {features.map((item) => (
                    <div
                        key={item.title}
                        className="rounded-lg border bg-white p-6 shadow"
                    >
                        <h2 className="mb-2 text-xl font-bold">
                            {item.title}
                        </h2>

                        <p className="text-gray-600">
                            {item.text}
                        </p>
                    </div>
                ))}
            </div>
        </div>
    );
}