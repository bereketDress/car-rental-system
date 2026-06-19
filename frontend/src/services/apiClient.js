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

export async function request(url, options = {}) {
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
        const message = typeof data === "object" && data?.message
            ? data.message
            : typeof data === "object" && data?.error
                ? data.error
                : `Request failed with status ${res.status}`;

        if (res.status === 401) {
            clearStoredAuth();
            throw new Error(`Unauthorized for ${url}. Please log in again.`);
        }

        throw new Error(message);
    }

    return { data };
}
