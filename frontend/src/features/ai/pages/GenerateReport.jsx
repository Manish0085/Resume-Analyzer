import React, { useState, useRef } from "react";
import { useNavigate, Link } from "react-router-dom";
import { generateReport } from "../services/ai.api";
import Navbar from "../components/Navbar";
import "../../../App.css";

const GenerateReport = () => {
    const [resume, setResume] = useState(null);
    const [selfDescription, setSelfDescription] = useState("");
    const [jobDescription, setJobDescription] = useState("");
    const [loading, setLoading] = useState(false);
    const [dragOver, setDragOver] = useState(false);
    const fileInputRef = useRef(null);
    const navigate = useNavigate();

    const handleFileChange = (file) => {
        if (file && (file.type === "application/pdf" || file.name.endsWith(".docx"))) {
            setResume(file);
        } else {
            alert("Please upload a PDF or DOCX file.");
        }
    };

    const handleDragOver = (e) => {
        e.preventDefault();
        setDragOver(true);
    };
    const handleDragLeave = () => setDragOver(false);
    const handleDrop = (e) => {
        e.preventDefault();
        setDragOver(false);
        const file = e.dataTransfer.files[0];
        if (file) handleFileChange(file);
    };

    const formatFileSize = (bytes) => {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1048576) return (bytes / 1024).toFixed(1) + " KB";
        return (bytes / 1048576).toFixed(1) + " MB";
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!resume || !jobDescription) {
            alert("Please upload a resume and provide a job description.");
            return;
        }

        const formData = new FormData();
        formData.append("resume", resume);
        formData.append("selfDescription", selfDescription);
        formData.append("jobDescription", jobDescription);

        setLoading(true);
        try {
            const data = await generateReport(formData);
            if (data && data.data) {
                navigate(`/ai/report/${data.data.id}`);
            }
        } catch (err) {
            console.error("Generation failed:", err);
            alert("Failed to generate report. Please try again.");
        } finally {
            setLoading(false);
        }
    };

    return (
        <>
            <Navbar />
            <main className="generate-report">
                <div className="container">
                    <div className="generate-report-inner">
                        <div className="page-header">
                            <h1>Upload Your Resume</h1>
                            <p>Upload your resume and paste the job description to get started.</p>
                        </div>

                        <form onSubmit={handleSubmit} className="generate-form" id="generate-report-form">

                            {/* Upload Zone */}
                            <div
                                className={`upload-zone ${resume ? "has-file" : ""} ${dragOver ? "drag-over" : ""}`}
                                onDragOver={handleDragOver}
                                onDragLeave={handleDragLeave}
                                onDrop={handleDrop}
                                onClick={() => !resume && fileInputRef.current?.click()}
                                id="resume-upload-zone"
                            >
                                <input
                                    ref={fileInputRef}
                                    type="file"
                                    id="resume-file-input"
                                    accept=".pdf,.docx"
                                    onChange={(e) => handleFileChange(e.target.files[0])}
                                    style={{ display: "none" }}
                                />

                                {resume ? (
                                    <div className="file-preview" onClick={(e) => e.stopPropagation()}>
                                        <span className="file-preview-icon">📄</span>
                                        <div className="file-preview-info">
                                            <div className="file-preview-name">{resume.name}</div>
                                            <div className="file-preview-size">{formatFileSize(resume.size)}</div>
                                        </div>
                                        <button
                                            type="button"
                                            className="file-preview-remove"
                                            onClick={() => setResume(null)}
                                            aria-label="Remove file"
                                        >
                                            ✕
                                        </button>
                                    </div>
                                ) : (
                                    <>
                                        <div className="upload-icon">⬆</div>
                                        <h3>Drag &amp; drop your resume here</h3>
                                        <p>or <span>click to browse</span> · PDF, DOCX</p>
                                    </>
                                )}
                            </div>

                            {/* Form Card */}
                            <div className="generate-form-card">
                                <div className="generate-form" style={{ gap: "1.25rem" }}>
                                    <div className="form-group">
                                        <label className="form-label" htmlFor="selfDescription">
                                            About You <span style={{ color: "var(--text-muted)", fontWeight: 400 }}>(optional)</span>
                                        </label>
                                        <textarea
                                            id="selfDescription"
                                            className="form-textarea"
                                            placeholder="Briefly describe your experience, skills, and background..."
                                            value={selfDescription}
                                            onChange={(e) => setSelfDescription(e.target.value)}
                                            style={{ minHeight: "100px" }}
                                        />
                                    </div>

                                    <div className="form-group">
                                        <label className="form-label" htmlFor="jobDescription">
                                            Job Description <span style={{ color: "var(--error)", fontSize: "0.8rem" }}>*</span>
                                        </label>
                                        <textarea
                                            id="jobDescription"
                                            className="form-textarea"
                                            placeholder="Paste the job description here..."
                                            value={jobDescription}
                                            onChange={(e) => setJobDescription(e.target.value)}
                                            required
                                        />
                                    </div>
                                </div>
                            </div>

                            {/* Actions */}
                            <div className="generate-form-actions">
                                <Link to="/" className="btn btn-secondary" id="cancel-btn">Cancel</Link>
                                <button
                                    type="submit"
                                    className="btn btn-primary btn-lg"
                                    disabled={loading}
                                    id="submit-report-btn"
                                >
                                    {loading ? (
                                        <>
                                            <span className="loading-spinner" style={{ width: 18, height: 18 }}></span>
                                            Analyzing...
                                        </>
                                    ) : (
                                        "✦ Analyze Resume"
                                    )}
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </main>
        </>
    );
};

export default GenerateReport;
