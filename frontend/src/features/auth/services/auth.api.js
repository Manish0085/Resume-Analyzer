import axios from "axios";

export const api = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8083/api/v1',
    withCredentials: true   // still needed for the refresh-token cookie
});

// ─── Token Management ─────────────────────────────────────────────────────────
let _accessToken = null;

export const setAccessToken = (token) => {
    _accessToken = token;
};

export const clearAccessToken = () => {
    _accessToken = null;
};

// ─── Request Interceptor — attach access token to every request ───────────────
api.interceptors.request.use((config) => {
    if (_accessToken) {
        config.headers["Authorization"] = `Bearer ${_accessToken}`;
    }
    return config;
});

// ─── Response Interceptor — auto-refresh on 401 ───────────────────────────────
// ─── Response Interceptor — auto-refresh on 401 ───────────────────────────────
let _refreshPromise = null;

api.interceptors.response.use(
    (response) => response,
    async (error) => {
        const originalRequest = error.config;

        // If 401 and we haven't retried yet, try refreshing
        if (
            error.response?.status === 401 &&
            !originalRequest._retry &&
            !originalRequest.url?.includes("/auth/refresh") &&
            !originalRequest.url?.includes("/auth/login")
        ) {
            originalRequest._retry = true;

            if (!_refreshPromise) {
                _refreshPromise = (async () => {
                    try {
                        const res = await api.post("/auth/refresh");
                        const newToken = res.data?.accessToken;
                        if (newToken) {
                            setAccessToken(newToken);
                            return newToken;
                        }
                        throw new Error("No token in refresh response");
                    } catch (refreshError) {
                        clearAccessToken();
                        // If refresh itself fails, the session is dead.
                        // The redirect to login is handled by the outer catch block
                        // that awaits _refreshPromise.
                        throw refreshError;
                    } finally {
                        _refreshPromise = null;
                    }
                })();
            }

            try {
                const token = await _refreshPromise;
                originalRequest.headers["Authorization"] = `Bearer ${token}`;
                return api(originalRequest);
            } catch (err) {
                // If refresh failed and this was a retry, go to login
                if (!window.location.pathname.startsWith("/login")) {
                    window.location.href = "/login";
                }
                return Promise.reject(err);
            }
        }

        return Promise.reject(error);
    }
);

// ─── Auth API Calls ────────────────────────────────────────────────────────────

export async function register(name, email, password) {
    try {
        const response = await api.post(`/auth/register`, { name, email, password });
        if (response.data?.accessToken) {
            setAccessToken(response.data.accessToken);
        }
        return response.data;
    } catch (err) {
        console.error("Register error:", err);
        throw err;
    }
}

export async function login(email, password) {
    try {
        const response = await api.post(`/auth/login`, { email, password });
        // Store the access token in memory right after login
        if (response.data?.accessToken) {
            setAccessToken(response.data.accessToken);
        }
        return response.data;
    } catch (err) {
        console.error("Login error:", err);
        throw err;
    }
}

export async function logout() {
    try {
        const response = await api.post(`/auth/logout`);
        clearAccessToken();
        return response.data;
    } catch (err) {
        console.error("Logout error:", err);
        clearAccessToken(); // clear even on error
        throw err;
    }
}

export async function getMe() {
    try {
        const response = await api.get(`/auth/get-me`);
        return response.data;
    } catch (err) {
        // console.error("GetMe error:", err);
        throw err;
    }
}

export async function refreshToken() {
    if (_refreshPromise) return _refreshPromise;

    _refreshPromise = (async () => {
        try {
            const response = await api.post(`/auth/refresh`);
            if (response.data?.accessToken) {
                setAccessToken(response.data.accessToken);
                return response.data;
            }
            throw new Error("Refresh failed");
        } catch (err) {
            clearAccessToken();
            throw err;
        } finally {
            _refreshPromise = null;
        }
    })();

    return _refreshPromise;
}