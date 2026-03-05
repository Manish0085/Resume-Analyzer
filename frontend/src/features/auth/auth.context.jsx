import { useState, useCallback, useMemo, useEffect, useRef } from "react";
import { AuthContext } from "./AuthContext";
import { refreshToken, getMe, setAccessToken, login, register, logout } from "./services/auth.api";

export const AuthProvider = ({ children }) => {
    const [user, setUserState] = useState(null);
    const [loading, setLoadingState] = useState(true); // start true while session restores
    const sessionRestored = useRef(false);

    // Memoize setters
    const setUser = useCallback((u) => setUserState(u), []);
    const setLoading = useCallback((l) => setLoadingState(l), []);

    useEffect(() => {
        if (sessionRestored.current) return;
        sessionRestored.current = true;

        const restoreSession = async () => {
            setLoading(true);
            try {
                const refreshData = await refreshToken();
                if (refreshData?.accessToken) {
                    setAccessToken(refreshData.accessToken);
                    const userData = await getMe();
                    if (userData) {
                        setUser(userData);
                    }
                }
            } catch (err) {
                // Not logged in or session expired
                setUser(null);
            } finally {
                setLoading(false);
            }
        };

        restoreSession();
    }, [setUser, setLoading]);

    const handleLogin = useCallback(async ({ email, password }) => {
        setLoadingState(true);
        try {
            const data = await login(email, password);
            if (data && data.userDto) {
                setUserState(data.userDto);
            }
            return data;
        } finally {
            setLoadingState(false);
        }
    }, []);

    const handleRegister = useCallback(async ({ name, email, password }) => {
        setLoadingState(true);
        try {
            const data = await register(name, email, password);
            if (data && data.userDto) {
                setUserState(data.userDto);
            }
            return data;
        } finally {
            setLoadingState(false);
        }
    }, []);

    const handleLogout = useCallback(async () => {
        setLoadingState(true);
        try {
            await logout();
            setUserState(null);
        } finally {
            setLoadingState(false);
        }
    }, []);

    const value = useMemo(
        () => ({ user, setUser, loading, setLoading, handleLogin, handleRegister, handleLogout }),
        [user, setUser, loading, setLoading, handleLogin, handleRegister, handleLogout]
    );

    return (
        <AuthContext.Provider value={value}>
            {children}
        </AuthContext.Provider>
    );
};