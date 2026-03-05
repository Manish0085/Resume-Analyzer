import React, { useState, useEffect } from "react";
import { useParams, Link } from "react-router-dom";
import { getReportById, downloadResumePdf } from "../services/ai.api";
import Navbar from "../components/Navbar";
import "../../../App.css";

const getPriorityClass = (priority) => {
    if (!priority) return "priority-medium";
    const p = priority.toLowerCase();
    if (p === "high") return "priority-high";
    if (p === "low") return "priority-low";
    return "priority-medium";
};

const getScoreColor = (score) => {
    if (score >= 70) return "#10b981";
    if (score >= 50) return "#f59e0b";
    return "#ef4444";
};

const ReportDetails = () => {
    const { reportId } = useParams();
    const [report, setReport] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchReport = async () => {
            try {
                const data = await getReportById(reportId);
                setReport(data.data);
            } catch (err) {
                console.error("Failed to fetch report:", err);
            } finally {
                setLoading(false);
            }
        };
        fetchReport();
    }, [reportId]);

    if (loading) {
        return (
            <>
                <Navbar />
                <div className="loading-page">
                    <div className="loading-spinner"></div>
                    <p>Loading report...</p>
                </div>
            </>
        );
    }

    if (!report) {
        return (
            <>
                <Navbar />
                <div className="loading-page">
                    <p>Report not found.</p>
                    <Link to="/" className="btn btn-secondary" style={{ marginTop: "1rem" }}>← Back to Dashboard</Link>
                </div>
            </>
        );
    }

    const scoreColor = getScoreColor(report.matchScore);

    return (
        <>
            <Navbar />
            <main className="report-details">
                <div className="container">
                    <div className="report-details-inner">
                        <div className="report-nav">
                            <Link to="/" className="back-link" id="back-to-dashboard">
                                ← Back to Dashboard
                            </Link>
                        </div>

                        {/* Hero */}
                        <div className="report-hero" id="report-hero">
                            <div className="report-hero-info">
                                <h1>{report.title || "Interview Report"}</h1>
                                <div style={{ display: "flex", gap: "0.75rem", flexWrap: "wrap", alignItems: "center" }}>
                                    <button
                                        onClick={() => downloadResumePdf(report.id)}
                                        className="btn btn-secondary btn-sm"
                                        id="download-pdf-btn"
                                    >
                                        ⬇ Download Optimized PDF
                                    </button>
                                </div>
                            </div>

                            <div className="report-score-display">
                                <div className="score-number" style={{ color: scoreColor }}>
                                    {report.matchScore}%
                                </div>
                                <div className="score-label">Match Score</div>
                                <span
                                    className={`badge ${report.matchScore >= 70 ? "badge-success" : report.matchScore >= 50 ? "badge-warning" : "badge-error"}`}
                                    style={{ marginTop: "0.5rem" }}
                                >
                                    {report.matchScore >= 70 ? "Strong Match" : report.matchScore >= 50 ? "Moderate" : "Needs Work"}
                                </span>
                            </div>
                        </div>

                        {/* Sections */}
                        <div className="report-sections">

                            {/* Skill Gaps */}
                            {report.skillGaps && report.skillGaps.length > 0 && (
                                <div className="report-section" id="skill-gaps-section">
                                    <div className="report-section-header">
                                        <div className="report-section-icon" style={{ background: "rgba(239,68,68,0.12)" }}>
                                            🎯
                                        </div>
                                        <h2>Skill Gaps</h2>
                                        <span className="badge badge-error" style={{ marginLeft: "auto" }}>
                                            {report.skillGaps.length} gap{report.skillGaps.length !== 1 ? "s" : ""}
                                        </span>
                                    </div>
                                    <div className="report-section-body">
                                        <div className="skill-gap-list">
                                            {report.skillGaps.map((gap, i) => (
                                                <div key={i} className="skill-gap-item" id={`skill-gap-${i}`}>
                                                    <div className={`priority-dot ${getPriorityClass(gap.priority)}`} />
                                                    <div>
                                                        <div className="skill-gap-name">{gap.skillName}</div>
                                                        <div className="skill-gap-desc">
                                                            {gap.description}
                                                            {gap.priority && (
                                                                <span style={{ marginLeft: "0.5rem", color: "var(--text-muted)", fontSize: "0.75rem" }}>
                                                                    · Priority: {gap.priority}
                                                                </span>
                                                            )}
                                                        </div>
                                                    </div>
                                                </div>
                                            ))}
                                        </div>
                                    </div>
                                </div>
                            )}

                            {/* Technical Questions */}
                            {report.technicalQuestions && report.technicalQuestions.length > 0 && (
                                <div className="report-section" id="technical-questions-section">
                                    <div className="report-section-header">
                                        <div className="report-section-icon" style={{ background: "rgba(99,102,241,0.12)" }}>
                                            💬
                                        </div>
                                        <h2>Technical Questions</h2>
                                        <span className="badge badge-primary" style={{ marginLeft: "auto" }}>
                                            {report.technicalQuestions.length} Q&A
                                        </span>
                                    </div>
                                    <div className="report-section-body">
                                        {report.technicalQuestions.map((q, i) => (
                                            <div key={i} className="question-item" id={`question-${i}`}>
                                                <div className="question-q">Q: {q.question}</div>
                                                <div className="question-a">A: {q.answer}</div>
                                            </div>
                                        ))}
                                    </div>
                                </div>
                            )}

                            {/* Preparation Plan */}
                            {report.preparationPlan && report.preparationPlan.length > 0 && (
                                <div className="report-section" id="preparation-plan-section">
                                    <div className="report-section-header">
                                        <div className="report-section-icon" style={{ background: "rgba(16,185,129,0.12)" }}>
                                            📅
                                        </div>
                                        <h2>Preparation Plan</h2>
                                        <span className="badge badge-success" style={{ marginLeft: "auto" }}>
                                            {report.preparationPlan.length} days
                                        </span>
                                    </div>
                                    <div className="report-section-body">
                                        {report.preparationPlan.map((p, i) => (
                                            <div key={i} className="plan-item" id={`plan-day-${i}`}>
                                                <div className="plan-day-num">{p.day}</div>
                                                <div className="plan-content">
                                                    <h3>{p.topic}</h3>
                                                    <p>{p.description}</p>
                                                </div>
                                            </div>
                                        ))}
                                    </div>
                                </div>
                            )}
                        </div>
                    </div>
                </div>
            </main>
        </>
    );
};

export default ReportDetails;
