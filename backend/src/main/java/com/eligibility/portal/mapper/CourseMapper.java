package com.eligibility.portal.mapper;

import com.eligibility.portal.catalog.Course;
import com.eligibility.portal.catalog.Stream;
import com.eligibility.portal.catalog.Subject;
import com.eligibility.portal.dto.response.CourseInfo;
import com.eligibility.portal.dto.response.StreamCoursesResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class CourseMapper {

    public CourseInfo toCourseInfo(Course course) {
        List<String> subjects = course.getRequiredSubjects().stream()
                .map(Subject::getDisplayName)
                .collect(Collectors.toList());
        return new CourseInfo(course.getDisplayName(), subjects, course.getCutoffPercentage(),
                course.isRequiresJee(), course.isRequiresNeet());
    }

    /** Groups the full catalog by stream, preserving catalog declaration order. */
    public List<StreamCoursesResponse> toGroupedByStream() {
        Map<Stream, List<CourseInfo>> grouped = new LinkedHashMap<>();
        for (Course course : Course.values()) {
            grouped.computeIfAbsent(course.getStream(), s -> new ArrayList<>()).add(toCourseInfo(course));
        }
        return grouped.entrySet().stream()
                .map(e -> new StreamCoursesResponse(e.getKey().name(), e.getValue()))
                .collect(Collectors.toList());
    }
}
