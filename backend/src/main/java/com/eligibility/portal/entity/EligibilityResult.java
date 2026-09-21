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
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** The computed eligibility outcome for a {@link StudentRequest} (one-to-one). */
@Entity
@Table(name = "eligibility_results")
@Getter
@Setter
@NoArgsConstructor
public class EligibilityResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_request_id", nullable = false, unique = true)
    private StudentRequest studentRequest;

    @Column(nullable = false)
    private boolean eligible;

    @Column(nullable = false, length = 500)
    private String reason;

    @Column(name = "evaluated_at", nullable = false)
    private LocalDateTime evaluatedAt;

    @OneToMany(mappedBy = "eligibilityResult", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("rankOrder ASC")
    private List<RecommendedCourse> recommendedCourses = new ArrayList<>();

    @PrePersist
    void onCreate() {
        if (evaluatedAt == null) {
            evaluatedAt = LocalDateTime.now();
        }
    }

    public void addRecommendedCourse(RecommendedCourse course) {
        recommendedCourses.add(course);
        course.setEligibilityResult(this);
    }
}
