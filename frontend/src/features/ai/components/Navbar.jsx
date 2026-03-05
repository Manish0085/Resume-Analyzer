import React, { useState, useEffect, useRef } from "react";
import { Link, useNavigate, useLocation } from "react-router-dom";
import { useAuth } from "../../auth/hooks/useAuth";

const Navbar = ({ isLanding = false }) => {
    const [menuOpen, setMenuOpen] = useState(false);
    const [scrolled, setScrolled] = useState(false);
    const { user, handleLogout } = useAuth();
    const navigate = useNavigate();
    const location = useLocation();
    const menuRef = useRef(null);

    // Close menu on route change
    useEffect(() => {
        setMenuOpen(false);
    }, [location]);

    // Handle scroll shadow
    useEffect(() => {
        const onScroll = () => setScrolled(window.scrollY > 10);
        window.addEventListener("scroll", onScroll, { passive: true });
        return () => window.removeEventListener("scroll", onScroll);
    }, []);

    // Close menu on outside click
    useEffect(() => {
        const handleClickOutside = (e) => {
            if (menuRef.current && !menuRef.current.contains(e.target)) {
                setMenuOpen(false);
            }
        };
        if (menuOpen) document.addEventListener("mousedown", handleClickOutside);
        return () => document.removeEventListener("mousedown", handleClickOutside);
    }, [menuOpen]);

    const handleLogoutClick = () => {
        setMenuOpen(false);
        handleLogout();
        navigate("/login");
    };

    const closeMenu = () => setMenuOpen(false);

    return (
        <nav className={`navbar${scrolled ? " navbar-scrolled" : ""}`} ref={menuRef}>
            <div className="container">
                {/* Brand */}
                <Link to="/" className="navbar-brand" onClick={closeMenu}>
                    <div className="navbar-logo-box">✦</div>
                    <span className="navbar-brand-name">ResumeAI</span>
                </Link>

                {/* Desktop nav links */}
                {isLanding ? (
                    <ul className="navbar-nav navbar-nav-desktop">
                        <li><a href="#home">Home</a></li>
                        <li><a href="#features">Features</a></li>
                        <li><a href="#how-it-works">How It Works</a></li>
                        {user && <li><Link to="/">Dashboard</Link></li>}
                    </ul>
                ) : (
                    <ul className="navbar-nav navbar-nav-desktop">
                        <li><Link to="/">Dashboard</Link></li>
                        <li><Link to="/ai/generate">New Report</Link></li>
                    </ul>
                )}

                {/* Desktop actions */}
                <div className="navbar-actions navbar-actions-desktop">
                    {user ? (
                        <>
                            <span className="navbar-username">Hi, {user.name?.split(" ")[0]}</span>
                            <button onClick={handleLogoutClick} className="btn btn-ghost btn-sm">
                                Sign Out
                            </button>
                        </>
                    ) : (
                        <>
                            <Link to="/login" className="btn btn-ghost btn-sm">Login</Link>
                            <Link to="/register" className="btn btn-primary btn-sm">Get Started</Link>
                        </>
                    )}
                </div>

                {/* Hamburger toggle */}
                <button
                    className={`navbar-toggle${menuOpen ? " is-open" : ""}`}
                    onClick={() => setMenuOpen(!menuOpen)}
                    aria-label="Toggle menu"
                    aria-expanded={menuOpen}
                    id="navbar-toggle-btn"
                >
                    <span></span>
                    <span></span>
                    <span></span>
                </button>
            </div>

            {/* Mobile drawer */}
            <div className={`navbar-mobile-menu${menuOpen ? " is-open" : ""}`}>
                <div className="navbar-mobile-menu-inner">
                    {isLanding ? (
                        <>
                            <a href="#home" className="mobile-nav-link" onClick={closeMenu}>Home</a>
                            <a href="#features" className="mobile-nav-link" onClick={closeMenu}>Features</a>
                            <a href="#how-it-works" className="mobile-nav-link" onClick={closeMenu}>How It Works</a>
                            {user && <Link to="/" className="mobile-nav-link" onClick={closeMenu}>Dashboard</Link>}
                        </>
                    ) : (
                        <>
                            <Link to="/" className="mobile-nav-link" onClick={closeMenu}>Dashboard</Link>
                            <Link to="/ai/generate" className="mobile-nav-link" onClick={closeMenu}>New Report</Link>
                        </>
                    )}

                    <div className="mobile-nav-divider"></div>

                    {user ? (
                        <>
                            <span className="mobile-nav-user">👤 {user.name}</span>
                            <button onClick={handleLogoutClick} className="btn btn-ghost btn-sm mobile-nav-action">
                                Sign Out
                            </button>
                        </>
                    ) : (
                        <>
                            <Link to="/login" className="btn btn-ghost btn-sm mobile-nav-action" onClick={closeMenu}>Login</Link>
                            <Link to="/register" className="btn btn-primary btn-sm mobile-nav-action" onClick={closeMenu}>Get Started Free</Link>
                        </>
                    )}
                </div>
            </div>
        </nav>
    );
};

export default Navbar;
