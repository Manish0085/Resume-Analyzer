package com.substring.auth.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import com.substring.auth.entity.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "interview_reports")
@EntityListeners(AuditingEntityListener.class)
public class InterviewReport {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // ← UUID generation
    @Column(updatable = false, nullable = false, columnDefinition = "VARCHAR(36)")
    private UUID id;

    @Column(name = "job_description", nullable = false, columnDefinition = "TEXT")
    private String jobDescription;

    @Column(columnDefinition = "TEXT")
    private String resume;

    @Column(name = "self_description", columnDefinition = "TEXT")
    private String selfDescription;

    @Min(0)
    @Max(100)
    @Column(name = "match_score")
    private Integer matchScore;

    @Column(nullable = false)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private User user;

    @OneToMany(mappedBy = "interviewReport", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TechnicalQuestion> technicalQuestions = new ArrayList<>();

    @OneToMany(mappedBy = "interviewReport", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BehavioralQuestion> behavioralQuestions = new ArrayList<>();

    @OneToMany(mappedBy = "interviewReport", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SkillGap> skillGaps = new ArrayList<>();

    @OneToMany(mappedBy = "interviewReport", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("day ASC")
    private List<PreparationPlan> preparationPlan = new ArrayList<>();

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public InterviewReport() {
    }

    public InterviewReport(UUID id, String jobDescription, String resume, String selfDescription, Integer matchScore,
            String title, com.substring.auth.entity.User user,
            List<com.substring.auth.entity.TechnicalQuestion> technicalQuestions,
            List<com.substring.auth.entity.BehavioralQuestion> behavioralQuestions,
            List<com.substring.auth.entity.SkillGap> skillGaps,
            List<com.substring.auth.entity.PreparationPlan> preparationPlan, LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        this.id = id;
        this.jobDescription = jobDescription;
        this.resume = resume;
        this.selfDescription = selfDescription;
        this.matchScore = matchScore;
        this.title = title;
        this.user = user;
        this.technicalQuestions = technicalQuestions;
        this.behavioralQuestions = behavioralQuestions;
        this.skillGaps = skillGaps;
        this.preparationPlan = preparationPlan;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public String getResume() {
        return resume;
    }

    public void setResume(String resume) {
        this.resume = resume;
    }

    public String getSelfDescription() {
        return selfDescription;
    }

    public void setSelfDescription(String selfDescription) {
        this.selfDescription = selfDescription;
    }

    public Integer getMatchScore() {
        return matchScore;
    }

    public void setMatchScore(Integer matchScore) {
        this.matchScore = matchScore;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<TechnicalQuestion> getTechnicalQuestions() {
        return technicalQuestions;
    }

    public void setTechnicalQuestions(List<TechnicalQuestion> technicalQuestions) {
        this.technicalQuestions = technicalQuestions;
    }

    public List<BehavioralQuestion> getBehavioralQuestions() {
        return behavioralQuestions;
    }

    public void setBehavioralQuestions(List<BehavioralQuestion> behavioralQuestions) {
        this.behavioralQuestions = behavioralQuestions;
    }

    public List<SkillGap> getSkillGaps() {
        return skillGaps;
    }

    public void setSkillGaps(List<SkillGap> skillGaps) {
        this.skillGaps = skillGaps;
    }

    public List<PreparationPlan> getPreparationPlan() {
        return preparationPlan;
    }

    public void setPreparationPlan(List<PreparationPlan> preparationPlan) {
        this.preparationPlan = preparationPlan;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}