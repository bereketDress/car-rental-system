import React, { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react';
import { authService, clearStoredAuth, getStoredAuth, storeAuth } from '../services/api';

const AuthContext = createContext(null);

function normalizeAuth(payload, fallbackToken = '') {
    if (payload?.authenticated === false) return null;

    const token = payload?.token ?? fallbackToken;
    if (!token) return null;

    return {
        token,
        role: payload.role ?? '',
        userId: payload.userId ?? null,
        name: payload.name ?? '',
        capabilities: Array.isArray(payload.capabilities) ? payload.capabilities : [],
    };
}

/**
 * @param {{ children?: React.ReactNode }} props
 */
export function AuthProvider({ children }) {
    const [auth, setAuth] = useState(() => getStoredAuth());

    const login = useCallback(async (credentials) => {
        const response = await authService.login(credentials);
        const nextAuth = normalizeAuth(response.data);

        if (!nextAuth) {
            throw new Error('Login response did not include a token.');
        }

        storeAuth(nextAuth);
        setAuth(nextAuth);
        return nextAuth;
    }, []);

    const register = useCallback(async (customer) => {
        const response = await authService.register(customer);
        const nextAuth = normalizeAuth(response.data);

        if (!nextAuth) {
            throw new Error('Registration response did not include a token.');
        }

        storeAuth(nextAuth);
        setAuth(nextAuth);
        return nextAuth;
    }, []);

    const logout = useCallback(() => {
        clearStoredAuth();
        setAuth(null);
    }, []);

    useEffect(() => {
        const handleAuthCleared = () => setAuth(null);
        window.addEventListener('crms:auth-cleared', handleAuthCleared);
        return () => window.removeEventListener('crms:auth-cleared', handleAuthCleared);
    }, []);

    const value = useMemo(() => ({
        auth,
        isAuthenticated: Boolean(auth?.token),
        login,
        register,
        logout,
    }), [auth, login, register, logout]);

    return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
    const context = useContext(AuthContext);
    if (!context) {
        throw new Error('useAuth must be used within an AuthProvider.');
    }
    return context;
}
