package com.eligibility.portal.controller;

import com.eligibility.portal.dto.response.StreamCoursesResponse;
import com.eligibility.portal.service.CourseCatalogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Tag(name = "Courses", description = "Public course catalog - the frontend's single source of truth for course rules")
public class CourseController {

    private final CourseCatalogService courseCatalogService;

    public CourseController(CourseCatalogService courseCatalogService) {
        this.courseCatalogService = courseCatalogService;
    }

    @Operation(summary = "Full course catalog grouped by stream, including required subjects, cutoff, and exam requirement")
    @GetMapping("/courses")
    public ResponseEntity<List<StreamCoursesResponse>> getCourses() {
        return ResponseEntity.ok(courseCatalogService.getCatalog());
    }
}
