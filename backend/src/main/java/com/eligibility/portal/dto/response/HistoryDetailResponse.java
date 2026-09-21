package com.eligibility.portal.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/** Full detail for one submission - staff drill-down from the (JWT-protected) history table. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HistoryDetailResponse {
    private Long id;
    private String studentName;
    private Integer age;
    private String gender;
    private List<SubjectMarkResponse> subjectMarks;
    private boolean jeeQualified;
    private boolean neetQualified;
    private String desiredCourse;
    private boolean eligible;
    private String eligibilityStatus;
    private String reason;
    private List<String> recommendedCourses;
    private LocalDateTime submittedAt;
    private LocalDateTime evaluatedAt;
}
