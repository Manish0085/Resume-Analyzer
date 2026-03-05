import React, { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../hooks/useAuth";
import { refreshToken } from "../services/auth.api";

const AuthSuccess = () => {
    const navigate = useNavigate();
    const { setUser, setLoading } = useAuth();

    useEffect(() => {
        const handleSuccess = async () => {
            setLoading(true);
            try {
                // After social login redirect, we have a refresh token cookie.
                // Call /refresh to get the access token and user info.
                const data = await refreshToken();
                if (data && data.userDto) {
                    setUser(data.userDto);
                } else if (data) {
                    setUser(data);
                }
                navigate("/");
            } catch (err) {
                console.error("Social login sync failed:", err);
                navigate("/login");
            } finally {
                setLoading(false);
            }
        };

        handleSuccess();
    }, [navigate, setUser, setLoading]);

    return (
        <main>
            <h1>Completing Login...</h1>
        </main>
    );
};

export default AuthSuccess;
