import { request } from "./apiClient";

export const branchService = {
    listAll: () => request("/api/branches"),
    create: (branch) =>
        request("/api/branches", {
            method: "POST",
            body: JSON.stringify(branch),
        }),
    update: (id, branch) =>
        request(`/api/branches/${id}`, {
            method: "PUT",
            body: JSON.stringify(branch),
        }),
    delete: (id) =>
        request(`/api/branches/${id}`, {
            method: "DELETE",
        }),
};
