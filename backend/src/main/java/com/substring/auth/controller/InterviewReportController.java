package com.substring.auth.controller;

import com.substring.auth.dto.ApiResponse;
import com.substring.auth.dto.InterviewReportSummaryDto;
import com.substring.auth.entity.InterviewReport;
import com.substring.auth.entity.User;
import com.substring.auth.service.InterviewReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/interview-reports")
@RequiredArgsConstructor
public class InterviewReportController {

        private final InterviewReportService interviewReportService;

        /**
         * Generate interview report based on user self description, resume and job
         * description.
         * Accepts multipart/form-data with a PDF file.
         */
        @PostMapping(value = "/generate", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        public ResponseEntity<ApiResponse<InterviewReport>> generateReport(
                        @RequestPart("resume") MultipartFile resumeFile,
                        @RequestPart(value = "selfDescription", required = false) String selfDescription,
                        @RequestPart("jobDescription") String jobDescription,
                        @AuthenticationPrincipal User user) {
                UUID userId = user.getId();

                InterviewReport report = interviewReportService.generateReport(
                                resumeFile, selfDescription, jobDescription, userId);

                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(new ApiResponse<>("Interview report generated successfully.", report));
        }

        /**
         * Get a specific interview report by ID (must belong to logged-in user).
         */
        @GetMapping("/{interviewId}")
        public ResponseEntity<ApiResponse<InterviewReport>> getReportById(
                        @PathVariable UUID interviewId,
                        @AuthenticationPrincipal User user) {
                UUID userId = user.getId();
                InterviewReport report = interviewReportService.getReportById(interviewId, userId);
                return ResponseEntity.ok(new ApiResponse<>("Interview report fetched successfully.", report));
        }

        /**
         * Get all interview reports of the logged-in user (summary view, no heavy
         * fields).
         */
        @GetMapping
        public ResponseEntity<ApiResponse<List<InterviewReportSummaryDto>>> getAllReports(
                        @AuthenticationPrincipal User user) {
                UUID userId = user.getId();
                List<InterviewReportSummaryDto> reports = interviewReportService.getAllReports(userId);
                return ResponseEntity.ok(new ApiResponse<>("Interview reports fetched successfully.", reports));
        }

        /**
         * Generate and download a resume PDF for a given interview report.
         */
        @GetMapping("/{interviewReportId}/resume-pdf")
        public ResponseEntity<byte[]> generateResumePdf(
                        @PathVariable UUID interviewReportId) {
                byte[] pdfBytes = interviewReportService.generateResumePdf(interviewReportId);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_PDF);
                headers.setContentDisposition(
                                ContentDisposition.attachment()
                                                .filename("resume_" + interviewReportId + ".pdf")
                                                .build());

                return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        }
}
