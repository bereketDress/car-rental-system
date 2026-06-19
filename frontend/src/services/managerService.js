import { request } from "./apiClient";

export const managerService = {
    reports: (branchId = "") => {
        const query = branchId ? `?branchId=${encodeURIComponent(branchId)}` : "";
        return request(`/api/manager/reports${query}`);
    },
    branchVehicles: (branchId) => request(`/api/manager/branches/${branchId}/vehicles`),
    addVehicle: (branchId, car) =>
        request(`/api/manager/branches/${branchId}/vehicles`, {
            method: "POST",
            body: JSON.stringify(car),
        }),
    updateVehicle: (carId, car) =>
        request(`/api/manager/vehicles/${carId}`, {
            method: "PUT",
            body: JSON.stringify(car),
        }),
    removeVehicle: (branchId, carId) =>
        request(`/api/manager/branches/${branchId}/vehicles/${carId}`, {
            method: "DELETE",
        }),
};
