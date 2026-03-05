package com.substring.auth.service;


import com.substring.auth.dto.*;

public interface AiService {
    AiReportResult generateInterviewReport(String resume, String selfDescription, String jobDescription);
    byte[] generateResumePdf(String resume, String selfDescription, String jobDescription);
}
