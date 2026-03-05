// Re-use the same axios instance that has the auth interceptors (Bearer token + auto-refresh)
// This ensures all AI API calls also carry the access token automatically.
import { api } from "../../../features/auth/services/auth.api";

// ── Shared api module ──
// We use the same configured axios instance for all protected calls.

export async function generateReport(formData) {
    try {
        const response = await api.post(`/interview-reports/generate`, formData, {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        });
        return response.data;
    } catch (err) {
        console.error("Generate report error:", err);
        throw err;
    }
}

export async function getAllReports() {
    try {
        const response = await api.get(`/interview-reports`);
        return response.data;
    } catch (err) {
        console.error("Get all reports error:", err);
        throw err;
    }
}

export async function getReportById(reportId) {
    try {
        const response = await api.get(`/interview-reports/${reportId}`);
        return response.data;
    } catch (err) {
        console.error("Get report by id error:", err);
        throw err;
    }
}

export async function downloadResumePdf(reportId) {
    try {
        const response = await api.get(`/interview-reports/${reportId}/resume-pdf`, {
            responseType: 'blob'
        });
        const url = window.URL.createObjectURL(new Blob([response.data]));
        const link = document.createElement('a');
        link.href = url;
        link.setAttribute('download', `resume_${reportId}.pdf`);
        document.body.appendChild(link);
        link.click();
        link.remove();
    } catch (err) {
        console.error("Download PDF error:", err);
        throw err;
    }
}

export { api };
