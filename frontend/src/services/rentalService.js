import { request } from "./apiClient";

export const rentalService = {
    list: () => request("/api/rentals"),
    checkout: (checkout) =>
        request("/api/rentals/checkout", {
            method: "POST",
            body: JSON.stringify(checkout),
        }),
    checkin: (id, checkin) =>
        request(`/api/rentals/${id}/checkin`, {
            method: "POST",
            body: JSON.stringify(checkin),
        }),
};
