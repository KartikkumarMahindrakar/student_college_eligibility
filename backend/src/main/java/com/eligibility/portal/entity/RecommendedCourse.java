package com.eligibility.portal.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/** One alternative-course suggestion belonging to an {@link EligibilityResult} (0-3 per result). */
@Entity
@Table(name = "recommended_courses", uniqueConstraints = @UniqueConstraint(columnNames = {"eligibility_result_id", "rank_order"}))
@Getter
@Setter
@NoArgsConstructor
public class RecommendedCourse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "eligibility_result_id", nullable = false)
    private EligibilityResult eligibilityResult;

    @Column(name = "course_name", nullable = false, length = 60)
    private String courseName;

    /** Preserves same-stream-first recommendation ordering across a DB round-trip. */
    @Column(name = "rank_order", nullable = false)
    private Integer rankOrder;

    public RecommendedCourse(String courseName, Integer rankOrder) {
        this.courseName = courseName;
        this.rankOrder = rankOrder;
    }
}
