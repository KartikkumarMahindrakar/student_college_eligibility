package com.eligibility.portal.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseWiseCount {
    private String courseName;
    private long total;
    private long eligible;
    private long notEligible;
}
