package com.substring.auth.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "preparation_plans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PreparationPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "plan_day", nullable = false)
    private Integer day;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String topic;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interview_report_id", nullable = false)
    @JsonIgnore
    private InterviewReport interviewReport;
}