package com.eligibility.portal.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/** One entry of GET /courses: a stream and the courses within it, in catalog order. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StreamCoursesResponse {
    private String stream;
    private List<CourseInfo> courses;
}
