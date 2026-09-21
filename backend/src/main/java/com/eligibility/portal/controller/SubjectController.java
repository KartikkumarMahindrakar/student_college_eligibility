package com.eligibility.portal.controller;

import com.eligibility.portal.service.SubjectCatalogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Tag(name = "Subjects", description = "Public master subject list")
public class SubjectController {

    private final SubjectCatalogService subjectCatalogService;

    public SubjectController(SubjectCatalogService subjectCatalogService) {
        this.subjectCatalogService = subjectCatalogService;
    }

    @Operation(summary = "The 13 subjects a student may report marks for")
    @GetMapping("/subjects")
    public ResponseEntity<List<String>> getSubjects() {
        return ResponseEntity.ok(subjectCatalogService.getSubjects());
    }
}
