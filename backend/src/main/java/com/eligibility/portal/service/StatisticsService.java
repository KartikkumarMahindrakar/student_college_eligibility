package com.eligibility.portal.service;

import com.eligibility.portal.dto.response.CourseWiseCount;
import com.eligibility.portal.dto.response.StatisticsResponse;
import com.eligibility.portal.repository.EligibilityResultRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StatisticsService {

    private final EligibilityResultRepository eligibilityResultRepository;

    public StatisticsService(EligibilityResultRepository eligibilityResultRepository) {
        this.eligibilityResultRepository = eligibilityResultRepository;
    }

    public StatisticsResponse getStatistics() {
        long eligibleCount = eligibilityResultRepository.countByEligible(true);
        long notEligibleCount = eligibilityResultRepository.countByEligible(false);

        List<CourseWiseCount> courseWiseCounts = new ArrayList<>();
        for (Object[] row : eligibilityResultRepository.countGroupedByCourse()) {
            courseWiseCounts.add(new CourseWiseCount((String) row[0], (Long) row[1], (Long) row[2], (Long) row[3]));
        }

        return new StatisticsResponse(eligibleCount + notEligibleCount, eligibleCount, notEligibleCount, courseWiseCounts);
    }
}
