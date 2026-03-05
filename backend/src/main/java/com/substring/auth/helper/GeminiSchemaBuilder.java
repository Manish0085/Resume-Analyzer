package com.substring.auth.helper;

import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Builds JSON schemas for Gemini structured output —
 * equivalent to zod + zodToJsonSchema in Node.js
 */
@Component
public class GeminiSchemaBuilder {

        public Map<String, Object> buildInterviewReportSchema() {
                Map<String, Object> schema = new LinkedHashMap<>();
                schema.put("type", "object");
                schema.put("required", List.of(
                                "matchScore", "technicalQuestions", "behavioralQuestions",
                                "skillGaps", "preparationPlan", "title"));

                Map<String, Object> properties = new LinkedHashMap<>();

                // matchScore
                properties.put("matchScore", Map.of(
                                "type", "integer",
                                "description",
                                "A score between 0 and 100 indicating how well the candidate's profile matches the job description"));

                // title
                properties.put("title", Map.of(
                                "type", "string",
                                "description", "The title of the job for which the interview report is generated"));

                // technicalQuestions
                properties.put("technicalQuestions", Map.of(
                                "type", "array",
                                "description", "Technical questions that can be asked in the interview",
                                "items", buildQuestionSchema("technical")));

                // behavioralQuestions
                properties.put("behavioralQuestions", Map.of(
                                "type", "array",
                                "description", "Behavioral questions that can be asked in the interview",
                                "items", buildQuestionSchema("behavioral")));

                // skillGaps
                properties.put("skillGaps", Map.of(
                                "type", "array",
                                "description", "List of skill gaps in the candidate's profile",
                                "items", Map.of(
                                                "type", "object",
                                                "required", List.of("skillName", "description", "priority"),
                                                "properties", Map.of(
                                                                "skillName",
                                                                Map.of("type", "string", "description",
                                                                                "The skill the candidate is lacking"),
                                                                "description",
                                                                Map.of("type", "string", "description",
                                                                                "Detailed explanation of the gap"),
                                                                "priority",
                                                                Map.of("type", "string", "enum",
                                                                                List.of("Low", "Medium", "High"),
                                                                                "description",
                                                                                "How critical this skill gap is")))));

                // preparationPlan
                properties.put("preparationPlan", Map.of(
                                "type", "array",
                                "description", "Day-wise preparation plan for the candidate",
                                "items", Map.of(
                                                "type", "object",
                                                "required", List.of("day", "topic", "description"),
                                                "properties", Map.of(
                                                                "day",
                                                                Map.of("type", "integer", "description",
                                                                                "Day number starting from 1"),
                                                                "topic",
                                                                Map.of("type", "string", "description",
                                                                                "Main topic for this day"),
                                                                "description", Map.of(
                                                                                "type", "string",
                                                                                "description",
                                                                                "What the candidate should do on this day")))));

                schema.put("properties", properties);
                return schema;
        }

        public Map<String, Object> buildResumePdfSchema() {
                Map<String, Object> schema = new LinkedHashMap<>();
                schema.put("type", "object");
                schema.put("required", List.of("html"));

                schema.put("properties", Map.of(
                                "html", Map.of(
                                                "type", "string",
                                                "description",
                                                "The HTML content of the resume which can be converted to PDF")));
                return schema;
        }

        private Map<String, Object> buildQuestionSchema(String type) {
                return Map.of(
                                "type", "object",
                                "required", List.of("question", "intention", "answer"),
                                "properties", Map.of(
                                                "question",
                                                Map.of("type", "string", "description", "The " + type + " question"),
                                                "intention",
                                                Map.of("type", "string", "description",
                                                                "The interviewer's intention behind this question"),
                                                "answer", Map.of("type", "string", "description",
                                                                "How to answer this question effectively")));
        }
}