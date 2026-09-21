package com.eligibility.portal.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** The original submission: personal details, exam status, desired course. */
@Entity
@Table(name = "student_requests")
@Getter
@Setter
@NoArgsConstructor
public class StudentRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_name", nullable = false, length = 100)
    private String studentName;

    @Column(nullable = false)
    private Integer age;

    @Column(nullable = false, length = 10)
    private String gender;

    @Column(name = "jee_qualified", nullable = false)
    private boolean jeeQualified;

    @Column(name = "neet_qualified", nullable = false)
    private boolean neetQualified;

    @Column(name = "desired_course", nullable = false, length = 60)
    private String desiredCourse;

    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt;

    @OneToMany(mappedBy = "studentRequest", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<SubjectMark> subjectMarks = new ArrayList<>();

    @OneToOne(mappedBy = "studentRequest", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private EligibilityResult eligibilityResult;

    @PrePersist
    void onCreate() {
        if (submittedAt == null) {
            submittedAt = LocalDateTime.now();
        }
    }

    public void addSubjectMark(SubjectMark mark) {
        subjectMarks.add(mark);
        mark.setStudentRequest(this);
    }

    public void assignEligibilityResult(EligibilityResult result) {
        this.eligibilityResult = result;
        result.setStudentRequest(this);
    }
}
