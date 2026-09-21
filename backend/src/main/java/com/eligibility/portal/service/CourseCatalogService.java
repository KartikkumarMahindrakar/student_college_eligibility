package com.eligibility.portal.service;

import com.eligibility.portal.dto.response.StreamCoursesResponse;
import com.eligibility.portal.mapper.CourseMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseCatalogService {

    private final CourseMapper courseMapper;

    public CourseCatalogService(CourseMapper courseMapper) {
        this.courseMapper = courseMapper;
    }

    public List<StreamCoursesResponse> getCatalog() {
        return courseMapper.toGroupedByStream();
    }
}
