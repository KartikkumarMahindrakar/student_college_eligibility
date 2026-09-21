package com.eligibility.portal.controller;

import com.eligibility.portal.dto.response.CourseInfo;
import com.eligibility.portal.dto.response.StreamCoursesResponse;
import com.eligibility.portal.security.JwtAuthenticationFilter;
import com.eligibility.portal.service.CourseCatalogService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CourseController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class))
@AutoConfigureMockMvc(addFilters = false)
class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CourseCatalogService courseCatalogService;

    @Test
    void getCourses_returnsCatalogGroupedByStream() throws Exception {
        CourseInfo cse = new CourseInfo("Computer Science Engineering",
                List.of("Physics", "Chemistry", "Mathematics"), 75.0, true, false);
        given(courseCatalogService.getCatalog())
                .willReturn(List.of(new StreamCoursesResponse("ENGINEERING", List.of(cse))));

        mockMvc.perform(get("/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].stream").value("ENGINEERING"))
                .andExpect(jsonPath("$[0].courses[0].name").value("Computer Science Engineering"))
                .andExpect(jsonPath("$[0].courses[0].requiresJee").value(true));
    }
}
