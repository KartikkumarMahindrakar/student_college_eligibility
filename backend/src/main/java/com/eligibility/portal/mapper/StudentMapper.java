package com.eligibility.portal.mapper;

import com.eligibility.portal.dto.request.CheckEligibilityRequest;
import com.eligibility.portal.dto.request.SubjectMarkRequest;
import com.eligibility.portal.dto.response.EligibilityResponse;
import com.eligibility.portal.dto.response.HistoryDetailResponse;
import com.eligibility.portal.dto.response.HistorySummaryResponse;
import com.eligibility.portal.dto.response.SubjectMarkResponse;
import com.eligibility.portal.entity.EligibilityResult;
import com.eligibility.portal.entity.RecommendedCourse;
import com.eligibility.portal.entity.StudentRequest;
import com.eligibility.portal.entity.SubjectMark;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class StudentMapper {

    public StudentRequest toEntity(CheckEligibilityRequest request) {
        StudentRequest entity = new StudentRequest();
        entity.setStudentName(request.getStudentName().trim());
        entity.setAge(request.getAge());
        entity.setGender(request.getGender());
        entity.setJeeQualified(Boolean.TRUE.equals(request.getJeeQualified()));
        entity.setNeetQualified(Boolean.TRUE.equals(request.getNeetQualified()));
        entity.setDesiredCourse(request.getDesiredCourse());
        for (SubjectMarkRequest markRequest : request.getSubjectMarks()) {
            entity.addSubjectMark(new SubjectMark(markRequest.getSubjectName(), markRequest.getMarks()));
        }
        return entity;
    }

    public EligibilityResponse toEligibilityResponse(StudentRequest studentRequest, EligibilityResult result) {
        return new EligibilityResponse(
                studentRequest.getId(),
                studentRequest.getStudentName(),
                studentRequest.getDesiredCourse(),
                result.isEligible(),
                result.getReason(),
                recommendedCourseNames(result),
                result.getEvaluatedAt());
    }

    public HistorySummaryResponse toHistorySummary(StudentRequest studentRequest) {
        EligibilityResult result = studentRequest.getEligibilityResult();
        return new HistorySummaryResponse(
                studentRequest.getId(),
                studentRequest.getStudentName(),
                studentRequest.getDesiredCourse(),
                statusLabel(result),
                studentRequest.getSubmittedAt());
    }

    public HistoryDetailResponse toHistoryDetail(StudentRequest studentRequest) {
        EligibilityResult result = studentRequest.getEligibilityResult();
        List<SubjectMarkResponse> marks = studentRequest.getSubjectMarks().stream()
                .map(m -> new SubjectMarkResponse(m.getSubjectName(), m.getMarks()))
                .collect(Collectors.toList());
        boolean eligible = result != null && result.isEligible();

        return new HistoryDetailResponse(
                studentRequest.getId(),
                studentRequest.getStudentName(),
                studentRequest.getAge(),
                studentRequest.getGender(),
                marks,
                studentRequest.isJeeQualified(),
                studentRequest.isNeetQualified(),
                studentRequest.getDesiredCourse(),
                eligible,
                statusLabel(result),
                result != null ? result.getReason() : null,
                recommendedCourseNames(result),
                studentRequest.getSubmittedAt(),
                result != null ? result.getEvaluatedAt() : null);
    }

    private List<String> recommendedCourseNames(EligibilityResult result) {
        if (result == null) {
            return Collections.emptyList();
        }
        return result.getRecommendedCourses().stream()
                .map(RecommendedCourse::getCourseName)
                .collect(Collectors.toList());
    }

    private String statusLabel(EligibilityResult result) {
        return result != null && result.isEligible() ? "Eligible" : "Not Eligible";
    }
}
