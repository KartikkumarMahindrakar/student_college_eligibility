package com.eligibility.portal.dto.request;

import com.eligibility.portal.validation.ValidSubjectName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class SubjectMarkRequest {

    @NotBlank(message = "Subject name is required")
    @ValidSubjectName
    private String subjectName;

    @NotNull(message = "Marks are required")
    @Min(value = 0, message = "Marks must be between 0 and 100")
    @Max(value = 100, message = "Marks must be between 0 and 100")
    private Integer marks;

    public SubjectMarkRequest(String subjectName, Integer marks) {
        this.subjectName = subjectName;
        this.marks = marks;
    }
}
