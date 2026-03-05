package com.substring.auth.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.substring.auth.dto.*;

import com.substring.auth.helper.*;
import com.substring.auth.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.ByteArrayOutputStream;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiServiceImpl implements AiService {

    @Value("${ai.openrouter.key}")
    private String apiKey;

    @Value("${ai.openrouter.url}")
    private String apiUrl;

    @Value("${ai.openrouter.model}")
    private String model;

    private final GeminiSchemaBuilder schemaBuilder;
    private final ObjectMapper objectMapper;
    private final WebClient webClient;

    // ─────────────────────────────────────────────
    // Generate Interview Report
    // ─────────────────────────────────────────────
    @Override
    public AiReportResult generateInterviewReport(String resume, String selfDescription, String jobDescription) {
        String prompt = String.format(
                """
                        <SYSTEM_INSTRUCTION>
                        You are an expert technical recruiter and career coach. Your task is to analyze a candidate's resume and self-description against a specific job description to provide a highly accurate and critical interview report.
                        - CRITICAL: Do NOT provide generic scores (like 85). Calculate the matchScore precisely based on actual keyword matching and experience relevance.
                        - CRITICAL: Detect the primary technology stack. If the resume is for MERN but the job is for Java, the matchScore should be very low.
                        - CRITICAL: All technical questions must be unique to the candidate's actual technology stack and the job's requirement.
                        - CRITICAL: The report must be in strict JSON format matching the schema provided.
                        </SYSTEM_INSTRUCTION>

                        INPUT DATA:
                        Resume Text: %s
                        Candidate Self-Description: %s
                        Target Job Description: %s

                        SCHEMA FOR JSON OUTPUT:
                        %s
                        """,
                resume, selfDescription, jobDescription, schemaBuilder.buildInterviewReportSchema());

        String jsonResponse = callAi(prompt);

        try {
            return objectMapper.readValue(jsonResponse, AiReportResult.class);
        } catch (Exception e) {
            log.error("Failed to parse AI interview report response: {}", jsonResponse, e);
            throw new RuntimeException("Failed to parse AI response for interview report", e);
        }
    }

    // ─────────────────────────────────────────────
    // Generate Resume PDF
    // ─────────────────────────────────────────────
    @Override
    public byte[] generateResumePdf(String resume, String selfDescription, String jobDescription) {
        String prompt = String.format(
                """
                        You are a professional resume writer and HTML/CSS designer. Generate a beautiful, clean, ATS-friendly resume in a SINGLE PAGE A4 format.

                        Candidate Information:
                        Resume Text: %s
                        Self Description: %s
                        Target Job Description: %s

                        STRICT REQUIREMENTS:
                        1. Output ONLY a valid JSON object with a single key "html".
                        2. The HTML must be self-contained XHTML — all tags MUST be properly self-closed (use <br/>, <meta ... />, <hr/>).
                        3. The resume MUST fit on exactly ONE single A4 page. This is non-negotiable.
                        4. Use the following CSS rules to enforce the page size and margins:
                           - @page { size: A4; margin: 10mm 12mm; }
                           - body { font-size: 9pt; font-family: Arial, sans-serif; line-height: 1.3; color: #222; margin: 0; padding: 0; }
                           - Keep all section headings and content compact with minimal padding and margins.
                        5. Design Guidelines for a single-page fit:
                           - Use a top header section with name (16pt, bold) and contact info in a single row.
                           - Section titles should be 10pt, bold, uppercase, with a bottom border, and only 4px margin top/bottom.
                           - Body text must be 8.5pt - 9pt.
                           - Use tight bullet points (margin: 1px 0).
                           - Do NOT use large padding or margin anywhere.
                           - Reduce sections to their most important bullet points (max 2-3 bullets per role/project).
                           - Skills section: list all skills inline, separated by commas, NOT as a list.
                        6. DO NOT use external resources (no external images, no external fonts, no CDN links).
                        7. Use a professional, modern color scheme. The name can be in a dark blue (#1a237e). Section headers in #1a237e. All other text in #222.
                        8. The output JSON must look like: {"html": "<html>...</html>"}
                        9. The resume should be in a professional, modern, and ATS-friendly format and must not be sound like AI generated.
                        """,
                resume, selfDescription, jobDescription);

        String jsonResponse = callAi(prompt);
        log.debug("AI Resume JSON Response: {}", jsonResponse);

        try {
            Map<?, ?> parsed = objectMapper.readValue(jsonResponse, Map.class);
            String htmlContent = (String) parsed.get("html");
            if (htmlContent == null || htmlContent.isBlank()) {
                throw new RuntimeException("AI returned empty HTML for resume");
            }
            return convertHtmlToPdf(htmlContent);
        } catch (Exception e) {
            log.error("Failed to generate resume PDF. AI Response was: {}", jsonResponse, e);
            throw new RuntimeException("Failed to generate resume PDF", e);
        }
    }

    // ─────────────────────────────────────────────
    // Shared: Call AI API (OpenRouter/OpenAI Format)
    // ─────────────────────────────────────────────
    private String callAi(String prompt) {
        OpenRouterRequest request = OpenRouterRequest.of(model, prompt);

        try {
            OpenRouterResponse response = webClient.post()
                    .uri(apiUrl)
                    .header("Authorization", "Bearer " + apiKey)
                    .header("HTTP-Referer", "http://localhost:8083") // Required by OpenRouter
                    .header("X-Title", "Resume Analyzer") // Optional for OpenRouter
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(OpenRouterResponse.class)
                    .block();

            if (response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
                throw new RuntimeException("Empty response from AI API");
            }

            String text = response.extractText();
            // Some models return JSON wrapped in markdown code blocks
            if (text != null && text.contains("```json")) {
                text = text.substring(text.indexOf("```json") + 7);
                text = text.substring(0, text.lastIndexOf("```"));
            } else if (text != null && text.contains("```")) {
                text = text.substring(text.indexOf("```") + 3);
                text = text.substring(0, text.lastIndexOf("```"));
            }
            return text;
        } catch (org.springframework.web.reactive.function.client.WebClientResponseException e) {
            log.error("AI API error: Status={}, Body={}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("AI Service Error: " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            log.error("Unexpected error calling AI API", e);
            throw e;
        }
    }

    // ─────────────────────────────────────────────
    // Shared: Convert HTML → PDF (replaces Puppeteer)
    // ─────────────────────────────────────────────
    private byte[] convertHtmlToPdf(String htmlContent) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            // openhtmltopdf is EXTREMELY picky about XHTML.
            // Even if AI promises XHTML, it might miss self-closing tags (like <meta>,
            // <br>, <img>).
            // Jsoup.parse() + OutputSettings.Syntax.xml fixes this automatically.

            Document document = Jsoup.parse(htmlContent);
            document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
            document.outputSettings().charset("UTF-8");
            String cleanedHtml = document.html();

            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(cleanedHtml, "http://localhost:8083/");
            builder.toStream(outputStream);
            builder.run();
            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error("Failed to convert HTML to PDF. HTML Content was: {}", htmlContent, e);
            throw new RuntimeException("Failed to convert HTML to PDF", e);
        }
    }
}
