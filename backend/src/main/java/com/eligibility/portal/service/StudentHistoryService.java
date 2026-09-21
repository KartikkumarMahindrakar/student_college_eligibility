package com.eligibility.portal.service;

import com.eligibility.portal.dto.response.HistoryDetailResponse;
import com.eligibility.portal.dto.response.HistorySummaryResponse;
import com.eligibility.portal.entity.StudentRequest;
import com.eligibility.portal.exception.ResourceNotFoundException;
import com.eligibility.portal.mapper.StudentMapper;
import com.eligibility.portal.repository.StudentRequestRepository;
import com.eligibility.portal.repository.StudentRequestSpecifications;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Backs the JWT-protected /students/history search/filter, detail, and export endpoints.
 *
 * <p>Every public method here is {@code @Transactional(readOnly = true)} because the entity
 * graph (StudentRequest -> EligibilityResult -> RecommendedCourse) is lazily fetched: mapping to
 * a response DTO must happen while the Hibernate session from the repository call is still open,
 * in the same method, rather than handing detached entities back to a caller that touches lazy
 * fields after the transaction has already closed.
 */
@Service
public class StudentHistoryService {

    private final StudentRequestRepository studentRequestRepository;
    private final StudentMapper studentMapper;

    public StudentHistoryService(StudentRequestRepository studentRequestRepository, StudentMapper studentMapper) {
        this.studentRequestRepository = studentRequestRepository;
        this.studentMapper = studentMapper;
    }

    @Transactional(readOnly = true)
    public List<HistorySummaryResponse> search(String name, String course, Boolean eligible,
                                                LocalDateTime from, LocalDateTime to) {
        return searchEntities(name, course, eligible, from, to).stream()
                .map(studentMapper::toHistorySummary)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public HistoryDetailResponse getDetail(Long id) {
        StudentRequest studentRequest = studentRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No submission found with id " + id));
        return studentMapper.toHistoryDetail(studentRequest);
    }

    private List<StudentRequest> searchEntities(String name, String course, Boolean eligible,
                                                 LocalDateTime from, LocalDateTime to) {
        Specification<StudentRequest> spec = Specification
                .where(StudentRequestSpecifications.hasStudentName(name))
                .and(StudentRequestSpecifications.hasCourse(course))
                .and(StudentRequestSpecifications.hasEligibilityStatus(eligible))
                .and(StudentRequestSpecifications.submittedBetween(from, to));
        return studentRequestRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "submittedAt"));
    }
}
