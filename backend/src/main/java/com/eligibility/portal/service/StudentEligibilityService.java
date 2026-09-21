package com.eligibility.portal.service;

import com.eligibility.portal.catalog.Course;
import com.eligibility.portal.catalog.Subject;
import com.eligibility.portal.dto.request.CheckEligibilityRequest;
import com.eligibility.portal.dto.request.SubjectMarkRequest;
import com.eligibility.portal.dto.response.EligibilityResponse;
import com.eligibility.portal.eligibility.EligibilityCheckResult;
import com.eligibility.portal.eligibility.EligibilityEvaluator;
import com.eligibility.portal.eligibility.RecommendationService;
import com.eligibility.portal.eligibility.SubjectMarks;
import com.eligibility.portal.entity.EligibilityResult;
import com.eligibility.portal.entity.RecommendedCourse;
import com.eligibility.portal.entity.StudentRequest;
import com.eligibility.portal.exception.ResourceNotFoundException;
import com.eligibility.portal.mapper.StudentMapper;
import com.eligibility.portal.repository.StudentRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/** Orchestrates POST /students/check-eligibility end to end, and the public single-result lookup. */
@Service
public class StudentEligibilityService {

    private final StudentRequestRepository studentRequestRepository;
    private final StudentMapper studentMapper;
    private final EligibilityEvaluator evaluator;
    private final RecommendationService recommendationService;

    public StudentEligibilityService(StudentRequestRepository studentRequestRepository,
                                      StudentMapper studentMapper,
                                      EligibilityEvaluator evaluator,
                                      RecommendationService recommendationService) {
        this.studentRequestRepository = studentRequestRepository;
        this.studentMapper = studentMapper;
        this.evaluator = evaluator;
        this.recommendationService = recommendationService;
    }

    @Transactional
    public EligibilityResponse checkEligibility(CheckEligibilityRequest request) {
        StudentRequest studentRequest = studentMapper.toEntity(request);

        Course course = Course.fromDisplayName(request.getDesiredCourse())
                .orElseThrow(() -> new IllegalStateException(
                        "Course passed validation but is not in the catalog: " + request.getDesiredCourse()));

        SubjectMarks marks = toSubjectMarks(request.getSubjectMarks());
        boolean jee = Boolean.TRUE.equals(request.getJeeQualified());
        boolean neet = Boolean.TRUE.equals(request.getNeetQualified());

        EligibilityCheckResult checkResult = evaluator.evaluate(course, marks, jee, neet);

        EligibilityResult resultEntity = new EligibilityResult();
        resultEntity.setEligible(checkResult.isEligible());
        resultEntity.setReason(checkResult.getReason());

        if (!checkResult.isEligible()) {
            List<Course> alternatives = recommendationService.recommendAlternatives(course, marks, jee, neet);
            int rank = 0;
            for (Course alternative : alternatives) {
                resultEntity.addRecommendedCourse(new RecommendedCourse(alternative.getDisplayName(), rank++));
            }
        }

        studentRequest.assignEligibilityResult(resultEntity);
        studentRequestRepository.save(studentRequest);

        return studentMapper.toEligibilityResponse(studentRequest, resultEntity);
    }

    /** Public lookup backing the Result screen's refresh/deep-link fallback. */
    @Transactional(readOnly = true)
    public EligibilityResponse getResultById(Long id) {
        StudentRequest studentRequest = studentRequestRepository.findById(id)
                .filter(sr -> sr.getEligibilityResult() != null)
                .orElseThrow(() -> new ResourceNotFoundException("No submission found with id " + id));
        return studentMapper.toEligibilityResponse(studentRequest, studentRequest.getEligibilityResult());
    }

    private SubjectMarks toSubjectMarks(List<SubjectMarkRequest> subjectMarkRequests) {
        Map<Subject, Integer> marksBySubject = new EnumMap<>(Subject.class);
        for (SubjectMarkRequest markRequest : subjectMarkRequests) {
            Subject subject = Subject.fromDisplayName(markRequest.getSubjectName())
                    .orElseThrow(() -> new IllegalStateException(
                            "Subject passed validation but is not in the catalog: " + markRequest.getSubjectName()));
            marksBySubject.put(subject, markRequest.getMarks());
        }
        return new SubjectMarks(marksBySubject);
    }
}
