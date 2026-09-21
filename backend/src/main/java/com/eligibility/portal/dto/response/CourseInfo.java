package com.eligibility.portal.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseInfo {
    private String name;
    private List<String> requiredSubjects;
    private Double cutoffPercentage;
    private boolean requiresJee;
    private boolean requiresNeet;
}
