import React from "react";
import { Link } from "react-router-dom";
import Navbar from "../components/Navbar";
import "../../../App.css";

const features = [
    {
        icon: "🎯",
        title: "ATS Score Analysis",
        desc: "Get a detailed ATS compatibility score and see how your resume performs against applicant tracking systems."
    },
    {
        icon: "🔍",
        title: "Skill Gap Detection",
        desc: "Identify missing skills and qualifications that recruiters are looking for in your target role."
    },
    {
        icon: "💡",
        title: "Keyword Suggestions",
        desc: "Receive intelligent keyword recommendations to optimize your resume for specific job descriptions."
    },
    {
        icon: "✨",
        title: "Resume Optimization",
        desc: "Get actionable suggestions to improve formatting, content, and overall resume effectiveness."
    }
];

const steps = [
    {
        num: "01",
        title: "Upload Resume",
        desc: "Upload your resume in PDF or DOCX format."
    },
    {
        num: "02",
        title: "Add Job Description",
        desc: "Paste the job description you're targeting."
    },
    {
        num: "03",
        title: "Get Analysis",
        desc: "Receive detailed insights and improvement suggestions."
    }
];

const LandingPage = () => {
    return (
        <div className="landing">
            <Navbar isLanding />

            {/* ── HERO ── */}
            <section className="hero" id="home">
                <div className="container">
                    <div className="hero-inner">
                        <div className="hero-badge">
                            <span>✦</span>
                            AI-Powered Resume Analysis
                        </div>

                        <h1>
                            Analyze Your Resume
                            <br />
                            <span className="gradient-text">with AI</span>
                        </h1>

                        <p>
                            Improve your chances of getting hired with intelligent resume analysis.
                            Compare against job descriptions and get actionable feedback.
                        </p>

                        <div className="hero-actions">
                            <Link to="/ai/generate" className="btn btn-primary btn-xl" id="hero-cta-btn">
                                Analyze Resume →
                            </Link>
                            <a href="#features" className="btn btn-secondary btn-xl" id="hero-learn-more-btn">
                                Learn More
                            </a>
                        </div>

                        <div className="hero-stats">
                            <div className="hero-stat">
                                <span className="check">✓</span>
                                <span>10k+ Resumes Analyzed</span>
                            </div>
                            <div className="hero-stat">
                                <span className="check">✓</span>
                                <span>95% User Satisfaction</span>
                            </div>
                            <div className="hero-stat">
                                <span className="check">✓</span>
                                <span>Free to Use</span>
                            </div>
                        </div>
                    </div>
                </div>
            </section>

            {/* ── FEATURES ── */}
            <section className="features" id="features">
                <div className="container">
                    <div className="section-header">
                        <h2>Powerful Resume Analysis</h2>
                        <p>Everything you need to optimize your resume and stand out to recruiters.</p>
                    </div>
                    <div className="features-grid">
                        {features.map((f, i) => (
                            <div key={i} className="feature-card" id={`feature-card-${i}`}>
                                <div className="feature-icon-box">{f.icon}</div>
                                <h3>{f.title}</h3>
                                <p>{f.desc}</p>
                            </div>
                        ))}
                    </div>
                </div>
            </section>

            {/* ── HOW IT WORKS ── */}
            <section className="how-it-works" id="how-it-works">
                <div className="container">
                    <div className="section-header">
                        <h2>How It Works</h2>
                        <p>Three simple steps to a better resume.</p>
                    </div>
                    <div className="steps-grid">
                        {steps.map((s, i) => (
                            <div key={i} className="step-card" id={`step-${i + 1}`}>
                                <div className="step-number">{s.num}</div>
                                <h3>{s.title}</h3>
                                <p>{s.desc}</p>
                            </div>
                        ))}
                    </div>
                </div>
            </section>

            {/* ── CTA ── */}
            <section className="cta-section">
                <div className="container">
                    <div className="cta-card">
                        <h2>Ready to Optimize Your Resume?</h2>
                        <p>Join thousands of job seekers who improved their resumes with ResumeAI.</p>
                        <Link to="/ai/generate" className="btn btn-primary btn-xl" id="cta-get-started-btn">
                            Get Started Free →
                        </Link>
                    </div>
                </div>
            </section>

            {/* ── FOOTER ── */}
            <footer className="footer">
                <div className="container">
                    <div className="footer-grid">
                        <div className="footer-brand">
                            <Link to="/" className="navbar-brand" style={{ marginBottom: "0.5rem" }}>
                                <div className="navbar-logo-box">✦</div>
                                <span className="navbar-brand-name">ResumeAI</span>
                            </Link>
                            <p>AI-powered resume analysis to help you land your dream job.</p>
                        </div>

                        <div className="footer-col">
                            <h4>Product</h4>
                            <ul>
                                <li><a href="#features">Features</a></li>
                                <li><a href="#how-it-works">How It Works</a></li>
                                <li><a href="#home">Pricing</a></li>
                                <li><a href="#home">API</a></li>
                            </ul>
                        </div>

                        <div className="footer-col">
                            <h4>Company</h4>
                            <ul>
                                <li><a href="#home">About</a></li>
                                <li><a href="#home">Blog</a></li>
                                <li><a href="#home">Careers</a></li>
                            </ul>
                        </div>

                        <div className="footer-col">
                            <h4>Support</h4>
                            <ul>
                                <li><a href="#home">Help Center</a></li>
                                <li><a href="#home">Contact</a></li>
                                <li><a href="#home">Privacy</a></li>
                            </ul>
                        </div>
                    </div>

                    <div className="footer-bottom">
                        <p>© 2026 ResumeAI. All rights reserved.</p>
                        <p>Built with ❤️ to help you land your dream job</p>
                    </div>
                </div>
            </footer>
        </div>
    );
};

export default LandingPage;
