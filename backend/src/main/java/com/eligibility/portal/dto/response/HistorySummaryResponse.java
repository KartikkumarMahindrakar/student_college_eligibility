package com.eligibility.portal.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/** One row of the Submission History table (Screen 3). */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HistorySummaryResponse {
    private Long id;
    private String studentName;
    private String courseApplied;
    private String eligibilityStatus;
    private LocalDateTime date;
}
