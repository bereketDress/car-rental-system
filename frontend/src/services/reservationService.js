import { request } from "./apiClient";

export const reservationService = {
    list: () => request("/api/reservations"),
    create: (reservation) =>
        request("/api/reservations", {
            method: "POST",
            body: JSON.stringify(reservation),
        }),
    confirm: (id) =>
        request(`/api/reservations/${id}/confirm`, {
            method: "PUT",
        }),
    cancel: (id) =>
        request(`/api/reservations/${id}/cancel`, {
            method: "POST",
        }),
};
