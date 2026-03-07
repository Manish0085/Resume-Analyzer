package com.substring.auth.repositroy;

import com.substring.auth.entity.*;
import com.substring.auth.dto.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InterviewReportRepository extends JpaRepository<InterviewReport, UUID> { // ← UUID

    Optional<InterviewReport> findByIdAndUser_Id(UUID id, UUID userId); // ← both UUID

    @Query("""
                SELECT new com.substring.auth.dto.InterviewReportSummaryDto(
                    r.id, r.title, r.matchScore, r.createdAt, r.updatedAt
                )
                FROM InterviewReport r
                WHERE r.user.id = :userId
                ORDER BY r.createdAt DESC
            """)
    List<InterviewReportSummaryDto> findSummaryByUserId(@Param("userId") UUID userId); // ← UUID
}