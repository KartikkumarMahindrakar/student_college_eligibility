package com.eligibility.portal.service;

import com.eligibility.portal.catalog.Subject;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubjectCatalogService {

    public List<String> getSubjects() {
        return Arrays.stream(Subject.values())
                .map(Subject::getDisplayName)
                .collect(Collectors.toList());
    }
}
