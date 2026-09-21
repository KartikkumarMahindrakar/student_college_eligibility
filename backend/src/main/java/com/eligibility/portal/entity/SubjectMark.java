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

/** One subject+marks entry belonging to a {@link StudentRequest} (six per request). */
@Entity
@Table(name = "subject_marks", uniqueConstraints = @UniqueConstraint(columnNames = {"student_request_id", "subject_name"}))
@Getter
@Setter
@NoArgsConstructor
public class SubjectMark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_request_id", nullable = false)
    private StudentRequest studentRequest;

    @Column(name = "subject_name", nullable = false, length = 30)
    private String subjectName;

    @Column(nullable = false)
    private Integer marks;

    public SubjectMark(String subjectName, Integer marks) {
        this.subjectName = subjectName;
        this.marks = marks;
    }
}
