package com.eligibility.portal.repository;

import com.eligibility.portal.entity.EligibilityResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EligibilityResultRepository extends JpaRepository<EligibilityResult, Long> {

    long countByEligible(boolean eligible);

    /** Each row: [desiredCourse, totalCount, eligibleCount, notEligibleCount]. */
    @Query("SELECT sr.desiredCourse, COUNT(er.id), "
            + "SUM(CASE WHEN er.eligible = true THEN 1L ELSE 0L END), "
            + "SUM(CASE WHEN er.eligible = false THEN 1L ELSE 0L END) "
            + "FROM EligibilityResult er JOIN er.studentRequest sr "
            + "GROUP BY sr.desiredCourse")
    List<Object[]> countGroupedByCourse();
}
