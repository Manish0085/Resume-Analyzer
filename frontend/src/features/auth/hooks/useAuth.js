import { useContext, useEffect, useRef } from "react";
import { AuthContext } from "../AuthContext";
import { refreshToken } from "../services/auth.api";

export const useAuth = () => {
    const context = useContext(AuthContext);
    const { user, setUser, loading, setLoading, handleLogin, handleRegister, handleLogout } = context;

    const handleRefresh = async () => {
        try {
            const data = await refreshToken();
            return data;
        } catch (err) {
            setUser(null);
            throw err;
        }
    };

    return { user, loading, handleRegister, handleLogin, handleLogout, handleRefresh };
};
