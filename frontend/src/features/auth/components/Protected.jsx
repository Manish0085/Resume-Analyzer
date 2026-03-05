import { useAuth } from "../hooks/useAuth";
import React from "react";
import { Navigate } from "react-router-dom";
import "../../../App.css";

const Protected = ({ children }) => {
    const { loading, user } = useAuth();

    // While session is being restored, show a full-page loader
    // (don't redirect yet — the user might have a valid refresh-token cookie)
    if (loading) {
        return (
            <div className="loading-page">
                <div className="loading-spinner"></div>
                <p>Loading...</p>
            </div>
        );
    }

    if (!user) {
        return <Navigate to="/login" replace />;
    }

    return children;
};

export default Protected;