import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../hooks/useAuth";
import "../../../App.css";

const Register = () => {
    const navigate = useNavigate();
    const [name, setName] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");
    const { loading, handleRegister } = useAuth();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError("");
        if (password.length < 6) {
            setError("Password must be at least 6 characters long.");
            return;
        }
        try {
            await handleRegister({ name, email, password });
            navigate("/");
        } catch (err) {
            setError("Registration failed. Please try again.");
        }
    };

    if (loading) {
        return (
            <div className="loading-page">
                <div className="loading-spinner"></div>
                <p>Creating your account...</p>
            </div>
        );
    }

    return (
        <div className="auth-page" id="register-page">
            <div className="auth-card">
                <Link to="/home" className="auth-logo" id="register-logo-link">
                    <div className="auth-logo-box">✦</div>
                    <span className="auth-logo-name">ResumeAI</span>
                </Link>

                <h1 className="auth-title">Create an account</h1>
                <p className="auth-subtitle">Start optimizing your resume with AI today</p>

                <form className="auth-form" onSubmit={handleSubmit} id="register-form">
                    {error && (
                        <div style={{
                            background: "rgba(239,68,68,0.1)",
                            border: "1px solid rgba(239,68,68,0.3)",
                            borderRadius: "var(--radius-md)",
                            padding: "0.75rem 1rem",
                            fontSize: "0.875rem",
                            color: "#f87171"
                        }}>
                            {error}
                        </div>
                    )}

                    <div className="form-group">
                        <label className="form-label" htmlFor="register-name">Full name</label>
                        <input
                            id="register-name"
                            type="text"
                            className="form-input"
                            placeholder="John Doe"
                            value={name}
                            onChange={(e) => setName(e.target.value)}
                            required
                            autoComplete="name"
                        />
                    </div>

                    <div className="form-group">
                        <label className="form-label" htmlFor="register-email">Email address</label>
                        <input
                            id="register-email"
                            type="email"
                            className="form-input"
                            placeholder="you@example.com"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            required
                            autoComplete="email"
                        />
                    </div>

                    <div className="form-group">
                        <label className="form-label" htmlFor="register-password">Password</label>
                        <input
                            id="register-password"
                            type="password"
                            className="form-input"
                            placeholder="Min. 6 characters"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                            autoComplete="new-password"
                        />
                    </div>

                    <button
                        type="submit"
                        className="btn btn-primary auth-submit"
                        disabled={loading}
                        id="register-submit-btn"
                    >
                        {loading ? (
                            <><span className="loading-spinner" style={{ width: 16, height: 16 }}></span> Creating account...</>
                        ) : (
                            "Create account"
                        )}
                    </button>
                </form>

                <div className="auth-footer">
                    Already have an account?{" "}
                    <Link to="/login" id="login-link">Sign in</Link>
                </div>
            </div>
        </div>
    );
};

export default Register;