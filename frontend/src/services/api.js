
// - Keep token for the current running UI session.
// - Send API requests.
// - Call backend APIs.

const BASE_URL = import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8081";
const AUTH_STORAGE_KEY = "crms_auth";
let currentAuth = readStoredAuth();

function readStoredAuth() {
    try {
        const raw = sessionStorage.getItem(AUTH_STORAGE_KEY) || localStorage.getItem(AUTH_STORAGE_KEY);
        return raw ? JSON.parse(raw) : null;
    } catch {
        return null;
    }
}

export function getStoredAuth() {
    return currentAuth;
}

export function storeAuth(auth) {
    currentAuth = auth;
    try {
        sessionStorage.setItem(AUTH_STORAGE_KEY, JSON.stringify(auth));
        localStorage.removeItem(AUTH_STORAGE_KEY);
    } catch {
        // Browser storage may be unavailable in restricted contexts.
    }
}

export function clearStoredAuth() {
    currentAuth = null;
    try {
        localStorage.removeItem(AUTH_STORAGE_KEY);
        sessionStorage.removeItem(AUTH_STORAGE_KEY);
    } catch {
        // Browser storage may be unavailable in restricted contexts.
    }
    window.dispatchEvent(new Event("crms:auth-cleared"));
}

// ---------- Common Request ----------
async function request(url, options = {}) {
    const token = getStoredAuth()?.token;
    const headers = {
        "Content-Type": "application/json",
        ...(options.headers ?? {}),
    };

    if (token) {
        headers.Authorization = `Bearer ${token}`;
    }

    const res = await fetch(BASE_URL + url, { ...options, headers });

    if (res.status === 204) {
        return { data: null };
    }

    const contentType = res.headers.get("content-type") ?? "";
    const data = contentType.includes("application/json") ? await res.json() : await res.text();

    if (!res.ok) {
        if (res.status === 401) {
            clearStoredAuth();
        }

        const message = typeof data === "object" && data?.message
            ? data.message
            : typeof data === "object" && data?.error
                ? data.error
                : `Request failed with status ${res.status}`;
        throw new Error(message);
    }

    return { data };
}

// ---------- Auth ----------
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

// ---------- Cars ----------
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
    update: (vinNo, car) =>
        request(`/api/cars/${vinNo}`, {
            method: "PUT",
            body: JSON.stringify(car),
        }),
    delete: (vinNo) =>
        request(`/api/cars/${vinNo}`, {
            method: "DELETE",
        }),
};

// ---------- Customers ----------
export const customerService = {
    register: (customer) =>
        request("/api/customers/register", {
            method: "POST",
            body: JSON.stringify(customer),
        }),
};

// ---------- Staff ----------
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

// ---------- Manager ----------
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
    updateVehicle: (vinNumber, car) =>
        request(`/api/manager/vehicles/${vinNumber}`, {
            method: "PUT",
            body: JSON.stringify(car),
        }),
    removeVehicle: (branchId, vinNumber) =>
        request(`/api/manager/branches/${branchId}/vehicles/${vinNumber}`, {
            method: "DELETE",
        }),
};

// ---------- Branches ----------
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

// ---------- Reservations ----------
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
        request(`/api/reservations/${id}`, {
            method: "DELETE",
        }),
};

// ---------- Rentals ----------
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

// ---------- Payments ----------
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

// ---------- Damages ----------
export const damageService = {
    listAll: () => request("/api/damages"),
    updateStatus: (id, status) =>
        request(`/api/damages/${id}/status`, {
            method: "PUT",
            body: JSON.stringify({ status }),
        }),
};
