package com.eligibility.portal.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/** Response for both POST /students/check-eligibility and GET /students/check-eligibility/{id}. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EligibilityResponse {
    private Long id;
    private String studentName;
    private String desiredCourse;
    private boolean eligible;
    private String reason;
    private List<String> recommendedCourses;
    private LocalDateTime evaluatedAt;
}
