package com.eligibility.portal.validation;

import com.eligibility.portal.dto.request.CheckEligibilityRequest;
import com.eligibility.portal.dto.request.SubjectMarkRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class CheckEligibilityRequestValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    private CheckEligibilityRequest validRequest() {
        CheckEligibilityRequest request = new CheckEligibilityRequest();
        request.setStudentName("Jane Doe");
        request.setAge(20);
        request.setGender("Female");
        request.setJeeQualified(true);
        request.setNeetQualified(false);
        request.setDesiredCourse("Computer Science Engineering");

        List<SubjectMarkRequest> marks = new ArrayList<>();
        marks.add(new SubjectMarkRequest("Physics", 80));
        marks.add(new SubjectMarkRequest("Chemistry", 80));
        marks.add(new SubjectMarkRequest("Mathematics", 80));
        marks.add(new SubjectMarkRequest("English", 80));
        marks.add(new SubjectMarkRequest("History", 80));
        marks.add(new SubjectMarkRequest("Geography", 80));
        request.setSubjectMarks(marks);
        return request;
    }

    @Test
    void validRequestHasNoViolations() {
        assertThat(validator.validate(validRequest())).isEmpty();
    }

    @Test
    void rejectsNameWithDigits() {
        CheckEligibilityRequest request = validRequest();
        request.setStudentName("John123");
        assertThat(violationFields(request)).contains("studentName");
    }

    @Test
    void rejectsNameWithDoubleSpaces() {
        CheckEligibilityRequest request = validRequest();
        request.setStudentName("John  Smith");
        assertThat(violationFields(request)).contains("studentName");
    }

    @Test
    void rejectsAllSpaceName() {
        CheckEligibilityRequest request = validRequest();
        request.setStudentName("   ");
        assertThat(violationFields(request)).contains("studentName");
    }

    @Test
    void acceptsNameWithSingleInternalSpaces() {
        CheckEligibilityRequest request = validRequest();
        request.setStudentName("Mary Jane Watson");
        assertThat(validator.validate(request)).isEmpty();
    }

    @Test
    void ageBoundaries() {
        assertThat(violationFields(withAge(16))).contains("age");
        assertThat(violationFields(withAge(17))).doesNotContain("age");
        assertThat(violationFields(withAge(25))).doesNotContain("age");
        assertThat(violationFields(withAge(26))).contains("age");
    }

    private CheckEligibilityRequest withAge(int age) {
        CheckEligibilityRequest request = validRequest();
        request.setAge(age);
        return request;
    }

    @Test
    void rejectsGenderWithWrongCasing() {
        CheckEligibilityRequest request = validRequest();
        request.setGender("male"); // exact case match required, no silent coercion
        assertThat(violationFields(request)).contains("gender");
    }

    @Test
    void marksBoundaries() {
        assertThat(violationFields(withFirstMark(-1))).contains("subjectMarks[0].marks");
        assertThat(violationFields(withFirstMark(0))).doesNotContain("subjectMarks[0].marks");
        assertThat(violationFields(withFirstMark(100))).doesNotContain("subjectMarks[0].marks");
        assertThat(violationFields(withFirstMark(101))).contains("subjectMarks[0].marks");
    }

    private CheckEligibilityRequest withFirstMark(int marks) {
        CheckEligibilityRequest request = validRequest();
        request.getSubjectMarks().get(0).setMarks(marks);
        return request;
    }

    @Test
    void rejectsUnknownCourse() {
        CheckEligibilityRequest request = validRequest();
        request.setDesiredCourse("Astrophysics");
        assertThat(violationFields(request)).contains("desiredCourse");
    }

    @Test
    void rejectsUnknownSubject() {
        CheckEligibilityRequest request = validRequest();
        request.getSubjectMarks().get(0).setSubjectName("Astrology");
        assertThat(violationFields(request)).contains("subjectMarks[0].subjectName");
    }

    @Test
    void rejectsWrongSubjectCount() {
        CheckEligibilityRequest request = validRequest();
        request.getSubjectMarks().remove(0);
        assertThat(violationFields(request)).contains("subjectMarks");
    }

    @Test
    void rejectsDuplicateSubjects() {
        CheckEligibilityRequest request = validRequest();
        request.getSubjectMarks().get(1).setSubjectName("Physics"); // duplicates index 0
        Set<ConstraintViolation<CheckEligibilityRequest>> violations = validator.validate(request);
        assertThat(violations).anyMatch(v -> v.getMessage().contains("Duplicate subject"));
    }

    private Set<String> violationFields(CheckEligibilityRequest request) {
        return validator.validate(request).stream()
                .map(v -> v.getPropertyPath().toString())
                .collect(Collectors.toSet());
    }
}
