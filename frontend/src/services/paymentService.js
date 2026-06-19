import { request } from "./apiClient";

export const paymentService = {
    listAll: () => request("/api/payments"),
    record: (payment) =>
        request("/api/payments/record", {
            method: "POST",
            body: JSON.stringify(payment),
        }),
    process: (payment) =>
        request("/api/payments/process", {
            method: "POST",
            body: JSON.stringify(payment),
        }),
    createIntent: (rentalId) =>
        request("/api/payments/create-intent", {
            method: "POST",
            body: JSON.stringify({ rentalId }),
        }),
    confirmCard: (paymentIntentId) =>
        request("/api/payments/confirm-card", {
            method: "POST",
            body: JSON.stringify({ paymentIntentId }),
        }),
    stripeConfig: () => request("/api/stripe/config"),
};
