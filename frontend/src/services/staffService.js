import { request } from "./apiClient";

export const staffService = {
    listAll: () => request("/api/staff"),
    create: (staff) =>
        request("/api/staff", {
            method: "POST",
            body: JSON.stringify(staff),
        }),
    update: (id, staff) =>
        request(`/api/staff/${id}`, {
            method: "PUT",
            body: JSON.stringify(staff),
        }),
    delete: (id) =>
        request(`/api/staff/${id}`, {
            method: "DELETE",
        }),
};
