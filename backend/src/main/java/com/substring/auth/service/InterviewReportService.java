package com.substring.auth.service;

import org.springframework.web.multipart.MultipartFile;
import com.substring.auth.entity.*;
import com.substring.auth.dto.*;

import java.util.List;
import java.util.UUID;

public interface InterviewReportService {

    InterviewReport generateReport(MultipartFile resume, String selfDescription, String jobDescription, UUID userId);

    InterviewReport getReportById(UUID interviewId, UUID userId);

    List<InterviewReportSummaryDto> getAllReports(UUID userId);

    byte[] generateResumePdf(UUID interviewReportId);
}
