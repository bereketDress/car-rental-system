import { request } from "./apiClient";

export const damageService = {
    listAll: () => request("/api/damages"),
    updateStatus: (id, status) =>
        request(`/api/damages/${id}/status`, {
            method: "PUT",
            body: JSON.stringify({ status }),
        }),
};
