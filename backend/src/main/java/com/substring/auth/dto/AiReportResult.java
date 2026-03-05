package com.substring.auth.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.substring.auth.entity.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AiReportResult {

    // Used in: report.setTitle(aiResult.getTitle())
    private String title;

    // Used in: report.setMatchScore(aiResult.getMatchScore())
    private Integer matchScore;

    // Used in: aiResult.getTechnicalQuestions().stream()...
    private List<TechnicalQuestion> technicalQuestions;

    // Used in: aiResult.getBehavioralQuestions().stream()...
    private List<BehavioralQuestion> behavioralQuestions;

    // Used in: aiResult.getSkillGaps().stream()...
    private List<SkillGap> skillGaps;

    // Used in: aiResult.getPreparationPlan().stream()...
    private List<PreparationPlan> preparationPlan;
}
