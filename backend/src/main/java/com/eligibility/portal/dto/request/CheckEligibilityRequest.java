package com.eligibility.portal.dto.request;

import com.eligibility.portal.validation.NoDuplicateSubjects;
import com.eligibility.portal.validation.ValidCourse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@NoDuplicateSubjects
public class CheckEligibilityRequest {

    @NotBlank(message = "Student name is required")
    @Pattern(regexp = "^[A-Za-z]+( [A-Za-z]+)*$", message = "Student name must contain alphabets and spaces only")
    private String studentName;

    @NotNull(message = "Age is required")
    @Min(value = 17, message = "Age must be between 17 and 25")
    @Max(value = 25, message = "Age must be between 17 and 25")
    private Integer age;

    @NotBlank(message = "Gender is required")
    @Pattern(regexp = "Male|Female|Other", message = "Gender must be one of Male, Female, Other")
    private String gender;

    @NotNull(message = "Subject marks are required")
    @Size(min = 6, max = 6, message = "Exactly 6 subject marks must be provided")
    @Valid
    private List<SubjectMarkRequest> subjectMarks;

    @NotNull(message = "JEE qualification status is required")
    private Boolean jeeQualified;

    @NotNull(message = "NEET qualification status is required")
    private Boolean neetQualified;

    @NotBlank(message = "Desired course is required")
    @ValidCourse
    private String desiredCourse;
}
