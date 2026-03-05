import React, { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import { getAllReports } from "../services/ai.api";
import { useAuth } from "../../auth/hooks/useAuth";
import Navbar from "../components/Navbar";
import "../../../App.css";

const Dashboard = () => {
    const [reports, setReports] = useState([]);
    const [loading, setLoading] = useState(true);
    const { user } = useAuth();

    useEffect(() => {
        const fetchReports = async () => {
            try {
                const data = await getAllReports();
                setReports(data.data || []);
            } catch (err) {
                console.error("Failed to fetch reports:", err);
            } finally {
                setLoading(false);
            }
        };
        if (user) fetchReports();
    }, [user]);

    if (loading) {
        return (
            <>
                <Navbar />
                <div className="loading-page">
                    <div className="loading-spinner"></div>
                    <p>Loading your reports...</p>
                </div>
            </>
        );
    }

    return (
        <>
            <Navbar />
            <main className="dashboard">
                <div className="container">
                    <header className="dashboard-header" id="dashboard-header">
                        <div>
                            <h1>
                                Welcome back,{" "}
                                <span>{user?.name?.split(" ")[0] || "there"}</span>
                            </h1>
                            <p style={{ marginTop: "0.35rem", fontSize: "0.9rem", color: "var(--text-muted)" }}>
                                Here are all your AI-generated resume reports.
                            </p>
                        </div>
                        <Link to="/ai/generate" className="btn btn-primary" id="new-report-btn">
                            ✦ New Report
                        </Link>
                    </header>

                    <section className="reports-list" id="reports-section">
                        <h2>{reports.length} Report{reports.length !== 1 ? "s" : ""}</h2>

                        {reports.length === 0 ? (
                            <div className="empty-state" id="empty-reports-state">
                                <div className="empty-state-icon">📄</div>
                                <h3 style={{ marginBottom: "0.5rem" }}>No reports yet</h3>
                                <p>Generate your first resume analysis to get started.</p>
                                <Link to="/ai/generate" className="btn btn-primary" id="empty-state-cta">
                                    ✦ Generate Report
                                </Link>
                            </div>
                        ) : (
                            <div className="reports-grid">
                                {reports.map((report) => (
                                    <Link
                                        key={report.id}
                                        to={`/ai/report/${report.id}`}
                                        className="report-card"
                                        id={`report-card-${report.id}`}
                                        style={{ textDecoration: "none" }}
                                    >
                                        <h3>{report.title || "Untitled Report"}</h3>
                                        <div className="report-card-meta">
                                            <span
                                                className={`badge ${report.matchScore >= 70 ? "badge-success" : report.matchScore >= 50 ? "badge-warning" : "badge-error"}`}
                                            >
                                                Match: {report.matchScore}%
                                            </span>
                                            <span className="report-card-date">
                                                {new Date(report.createdAt).toLocaleDateString("en-US", {
                                                    month: "short", day: "numeric", year: "numeric"
                                                })}
                                            </span>
                                        </div>
                                        <span className="report-card-link">
                                            View Details →
                                        </span>
                                    </Link>
                                ))}
                            </div>
                        )}
                    </section>
                </div>
            </main>
        </>
    );
};

export default Dashboard;
