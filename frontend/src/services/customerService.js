import { request } from "./apiClient";

export const customerService = {
    register: (customer) =>
        request("/api/customers/register", {
            method: "POST",
            body: JSON.stringify(customer),
        }),
};
