import { request } from "./apiClient";

export const authService = {
    login: (data) =>
        request("/api/auth/login", {
            method: "POST",
            body: JSON.stringify(data),
        }),
    register: (data) =>
        request("/api/auth/register", {
            method: "POST",
            body: JSON.stringify(data),
        }),
    me: () => request("/api/auth/me"),
};
