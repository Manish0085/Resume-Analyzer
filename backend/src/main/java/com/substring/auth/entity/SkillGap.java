package com.substring.auth.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "skill_gaps")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SkillGap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "skill_name", nullable = false)
    private String skillName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String priority; // High, Medium, Low

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interview_report_id", nullable = false)
    @JsonIgnore
    private InterviewReport interviewReport;
}
