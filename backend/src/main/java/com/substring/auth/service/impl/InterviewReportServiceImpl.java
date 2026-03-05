package com.substring.auth.service.impl;

import com.substring.auth.dto.*;
import com.substring.auth.repositroy.*;
import com.substring.auth.service.*;
import com.substring.auth.exception.*;
import com.substring.auth.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InterviewReportServiceImpl implements InterviewReportService {

        private final InterviewReportRepository interviewReportRepository;
        private final AiService aiService;
        private final PdfParserService pdfParserService;
        private final EmailService emailService;

        @Override
        public InterviewReport generateReport(MultipartFile resumeFile, String selfDescription,
                        String jobDescription, UUID userId) {
                // 1. Extract text from uploaded PDF
                String resumeText = pdfParserService.extractText(resumeFile);

                // 2. Call AI service to generate report content
                AiReportResult aiResult = aiService.generateInterviewReport(resumeText, selfDescription,
                                jobDescription);

                // 3. Build and persist the entity
                InterviewReport report = new InterviewReport();
                report.setResume(resumeText);
                report.setSelfDescription(selfDescription);
                report.setJobDescription(jobDescription);
                report.setTitle(aiResult.getTitle());
                report.setMatchScore(aiResult.getMatchScore());

                // Map child collections and set back-references
                List<TechnicalQuestion> technicalQuestions = aiResult.getTechnicalQuestions().stream()
                                .peek(q -> q.setInterviewReport(report))
                                .collect(Collectors.toList());

                List<BehavioralQuestion> behavioralQuestions = aiResult.getBehavioralQuestions().stream()
                                .peek(q -> q.setInterviewReport(report))
                                .collect(Collectors.toList());

                List<SkillGap> skillGaps = aiResult.getSkillGaps().stream()
                                .peek(s -> s.setInterviewReport(report))
                                .collect(Collectors.toList());

                List<PreparationPlan> preparationPlan = aiResult.getPreparationPlan().stream()
                                .peek(p -> p.setInterviewReport(report))
                                .collect(Collectors.toList());

                report.setTechnicalQuestions(technicalQuestions);
                report.setBehavioralQuestions(behavioralQuestions);
                report.setSkillGaps(skillGaps);
                report.setPreparationPlan(preparationPlan);

                User user = new User();
                user.setId(userId);
                report.setUser(user);

                return interviewReportRepository.save(report);
        }

        @Override
        public InterviewReport getReportById(UUID interviewId, UUID userId) {
                return interviewReportRepository
                                .findByIdAndUserId(interviewId, userId)
                                .orElseThrow(() -> new ResourceNotFoundException("Interview report not found."));
        }

        @Override
        public List<InterviewReportSummaryDto> getAllReports(UUID userId) {
                return interviewReportRepository.findSummaryByUserId(userId);
        }

        @Override
        @Transactional
        public byte[] generateResumePdf(UUID interviewReportId) {
                InterviewReport report = interviewReportRepository.findById(interviewReportId)
                                .orElseThrow(() -> new ResourceNotFoundException("Interview report not found."));

                byte[] pdfByte = aiService.generateResumePdf(
                                report.getResume(),
                                report.getJobDescription(),
                                report.getSelfDescription());

                emailService.sendPdfByEmail(report.getUser().getEmail(), "Interview Report", pdfByte);

                return pdfByte;
        }
}
