import { request } from "./apiClient";

export const carService = {
    listAll: () => request("/api/cars"),
    searchAvailable: (type = "") => {
        const params = new URLSearchParams();
        if (type.trim()) {
            params.set("type", type.trim());
        }

        const query = params.toString();
        return request(`/api/cars/search${query ? `?${query}` : ""}`);
    },
    create: (car) =>
        request("/api/cars", {
            method: "POST",
            body: JSON.stringify(car),
        }),
    update: (carId, car) =>
        request(`/api/cars/${carId}`, {
            method: "PUT",
            body: JSON.stringify(car),
        }),
    delete: (carId) =>
        request(`/api/cars/${carId}`, {
            method: "DELETE",
        }),
};
